package com.devicelife.service;

import com.devicelife.dto.DeviceReportResponse;
import com.devicelife.entity.Device;
import com.devicelife.entity.DeviceEvent;
import com.devicelife.entity.DeviceHealth;
import com.devicelife.entity.DeviceUsage;
import com.devicelife.repository.DeviceEventRepository;
import com.devicelife.repository.DeviceHealthRepository;
import com.devicelife.repository.DeviceUsageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeviceAnalysisService {
    private final DeviceService deviceService;
    private final DeviceEventRepository eventRepository;
    private final DeviceHealthRepository healthRepository;
    private final DeviceUsageRepository usageRepository;
    private final ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Value("${groq.base-url:https://api.groq.com/openai/v1}")
    private String groqBaseUrl;

    @org.springframework.beans.factory.annotation.Value("${groq.api-key:}")
    private String groqApiKey;

    @org.springframework.beans.factory.annotation.Value("${groq.model:openai/gpt-oss-20b}")
    private String groqModel;

    /**
     * Produces a local, deterministic report from values already stored in MySQL.
     * It intentionally does not call an external model or invent missing metrics.
     */
    public DeviceReportResponse analyze(Long deviceId, Long userId) {
        Device device = deviceService.getDeviceForUser(deviceId, userId);
        DeviceHealth health = healthRepository.findTopByDeviceIdOrderByCheckedAtDesc(deviceId).orElse(null);
        List<DeviceEvent> events = eventRepository.findByDeviceIdOrderByEventDateDesc(deviceId);
        List<DeviceUsage> usage = usageRepository.findByDeviceIdOrderByUsageDateDesc(deviceId);

        List<String> findings = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        BigDecimal score = calculateScore(health, findings, recommendations);
        BigDecimal eventCost = events.stream()
                .map(event -> event.getCost() == null ? BigDecimal.ZERO : event.getCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal purchasePrice = valueOrZero(device.getPurchasePrice());
        BigDecimal totalCost = purchasePrice.add(eventCost);
        BigDecimal usageHours = usage.stream()
                .map(item -> item.getUsageHours() == null ? BigDecimal.ZERO : item.getUsageHours())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (health == null) {
            findings.add("数据库中暂无 PowerShell 或健康采集记录，当前评分不足以判断设备健康状态。");
            recommendations.add("在 Windows PowerShell 中运行 scripts/Collect-DeviceInfo.ps1 后重新生成报告。");
        }

        DeviceReportResponse response = new DeviceReportResponse();
        response.setDeviceId(device.getDeviceId());
        response.setDeviceName(device.getDeviceName());
        response.setGeneratedAt(LocalDateTime.now());
        response.setGeneratedBy("local-rules");
        response.setScore(score);
        response.setRating(ratingFor(score));
        response.setSummary(summaryFor(device, score, health, events.size()));
        response.setFindings(findings);
        response.setRecommendations(recommendations);
        response.setLatestHealth(health);
        response.setPurchasePrice(device.getPurchasePrice());
        response.setCurrentValue(device.getCurrentValue());
        response.setEventCost(eventCost);
        response.setTotalCost(totalCost);
        response.setEventCount(events.size());
        response.setTotalUsageHours(usageHours);
        return enrichWithGroq(response, device, health, events);
    }

    private DeviceReportResponse enrichWithGroq(DeviceReportResponse fallback,
                                                 Device device,
                                                 DeviceHealth health,
                                                 List<DeviceEvent> events) {
        if (groqApiKey == null || groqApiKey.isBlank()) {
            return fallback;
        }

        try {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("model", groqModel);
            request.put("temperature", 0.2);
            request.put("max_tokens", 700);
            request.put("response_format", Map.of("type", "json_object"));
            request.put("messages", List.of(
                    Map.of("role", "system", "content", "你是 DeviceLife 的设备分析助手。只根据用户提供的数据库 JSON 分析，不得补造缺失数据。请用中文输出，严格只返回 JSON，字段为 score(0到100的数字或null)、rating、summary、findings(字符串数组)、recommendations(字符串数组)。没有健康采集数据时 score 必须为 null，rating 必须为数据不足。"),
                    Map.of("role", "user", "content", objectMapper.writeValueAsString(toAnalysisInput(device, health, events)))
            ));

            JsonNode root = RestClient.create(groqBaseUrl)
                    .post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + groqApiKey)
                    .header("Content-Type", "application/json")
                    .body(request)
                    .retrieve()
                    .body(JsonNode.class);
            JsonNode content = root == null ? null : root.path("choices").path(0).path("message").path("content");
            if (content == null || content.isMissingNode() || content.asText().isBlank()) {
                return fallback;
            }

            JsonNode report = objectMapper.readTree(stripCodeFence(content.asText()));
            if (!report.isObject()) {
                return fallback;
            }
            if (report.has("score") && !report.get("score").isNull()) {
                BigDecimal score = report.get("score").decimalValue();
                if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.valueOf(100)) > 0) {
                    return fallback;
                }
                fallback.setScore(score.setScale(2, RoundingMode.HALF_UP));
            }
            if (report.hasNonNull("rating")) fallback.setRating(report.get("rating").asText());
            if (report.hasNonNull("summary")) fallback.setSummary(report.get("summary").asText());
            if (report.has("findings") && report.get("findings").isArray()) {
                fallback.setFindings(toStringList(report.get("findings")));
            }
            if (report.has("recommendations") && report.get("recommendations").isArray()) {
                fallback.setRecommendations(toStringList(report.get("recommendations")));
            }
            fallback.setGeneratedBy("groq/" + groqModel);
        } catch (Exception ignored) {
            // Keep the deterministic local report when Groq is unavailable or returns invalid JSON.
        }
        return fallback;
    }

    private Map<String, Object> toAnalysisInput(Device device, DeviceHealth health, List<DeviceEvent> events) {
        Map<String, Object> input = new LinkedHashMap<>();
        Map<String, Object> deviceInput = new LinkedHashMap<>();
        deviceInput.put("deviceName", device.getDeviceName());
        deviceInput.put("deviceType", device.getDeviceType());
        deviceInput.put("manufacturer", valueOrUnknown(device.getManufacturer()));
        deviceInput.put("model", valueOrUnknown(device.getModel()));
        deviceInput.put("operatingSystem", valueOrUnknown(device.getOperatingSystem()));
        deviceInput.put("cpu", valueOrUnknown(device.getCpu()));
        deviceInput.put("ramGb", device.getRamGb());
        deviceInput.put("storageGb", device.getStorageGb());
        deviceInput.put("purchasePrice", device.getPurchasePrice());
        deviceInput.put("currentValue", device.getCurrentValue());
        deviceInput.put("status", valueOrUnknown(device.getStatus()));
        input.put("device", deviceInput);
        input.put("latestHealth", health);
        input.put("events", events.stream().map(event -> {
            Map<String, Object> eventInput = new LinkedHashMap<>();
            eventInput.put("eventType", event.getEventType());
            eventInput.put("eventDate", event.getEventDate());
            eventInput.put("cost", event.getCost());
            return eventInput;
        }).toList());
        return input;
    }

    private List<String> toStringList(JsonNode array) {
        List<String> values = new ArrayList<>();
        array.forEach(item -> {
            if (item.isTextual() && !item.asText().isBlank()) values.add(item.asText());
        });
        return values;
    }

    private String stripCodeFence(String content) {
        String value = content.trim();
        if (value.startsWith("```")) {
            value = value.replaceFirst("^```(?:json)?\\s*", "");
            value = value.replaceFirst("\\s*```$", "");
        }
        return value.trim();
    }

    private String valueOrUnknown(String value) {
        return value == null ? "未知" : value;
    }

    private BigDecimal calculateScore(DeviceHealth health, List<String> findings, List<String> recommendations) {
        if (health == null) {
            return null;
        }

        int observedMetrics = 0;
        int deductions = 0;

        if (health.getCpuUsage() != null) {
            observedMetrics++;
            double value = health.getCpuUsage().doubleValue();
            if (value > 90) {
                deductions += 25;
                findings.add("CPU 当前负载超过 90%。");
                recommendations.add("检查后台高负载进程并确认散热状态。");
            } else if (value > 75) {
                deductions += 15;
                findings.add("CPU 当前负载偏高。");
                recommendations.add("观察 CPU 负载是否持续偏高。");
            } else if (value > 60) {
                deductions += 8;
            }
        }

        if (health.getMemoryUsage() != null) {
            observedMetrics++;
            double value = health.getMemoryUsage().doubleValue();
            if (value > 90) {
                deductions += 25;
                findings.add("内存使用率超过 90%。");
                recommendations.add("关闭不必要的后台应用，必要时增加内存容量。");
            } else if (value > 80) {
                deductions += 15;
                findings.add("内存使用率偏高。");
                recommendations.add("清理不必要的常驻程序并观察内存使用趋势。");
            } else if (value > 65) {
                deductions += 8;
            }
        }

        if (health.getDiskUsage() != null) {
            observedMetrics++;
            double value = health.getDiskUsage().doubleValue();
            if (value > 95) {
                deductions += 25;
                findings.add("磁盘使用率超过 95%。");
                recommendations.add("尽快清理或迁移大文件，避免系统盘空间不足。");
            } else if (value > 85) {
                deductions += 15;
                findings.add("磁盘使用率偏高。");
                recommendations.add("安排磁盘清理并保留足够可用空间。");
            } else if (value > 75) {
                deductions += 8;
            }
        }

        if (health.getCpuTemperature() != null) {
            observedMetrics++;
            double value = health.getCpuTemperature().doubleValue();
            if (value > 90) {
                deductions += 30;
                findings.add("CPU 温度超过 90°C。");
                recommendations.add("立即检查风扇、散热器和通风环境。");
            } else if (value > 80) {
                deductions += 20;
                findings.add("CPU 温度偏高。");
                recommendations.add("检查散热条件并减少持续高负载任务。");
            } else if (value > 70) {
                deductions += 10;
            }
        }

        if (health.getBatteryHealth() != null) {
            observedMetrics++;
            double value = health.getBatteryHealth().doubleValue();
            if (value < 50) {
                deductions += 25;
                findings.add("电池健康度低于 50%。");
                recommendations.add("建议评估电池更换计划。");
            } else if (value < 70) {
                deductions += 15;
                findings.add("电池健康度偏低。");
                recommendations.add("减少高温和长期满电使用，并关注续航变化。");
            } else if (value < 85) {
                deductions += 8;
            }
        }

        if (health.getBatteryCycleCount() != null) {
            observedMetrics++;
            int value = health.getBatteryCycleCount();
            if (value > 1000) {
                deductions += 15;
                findings.add("电池循环次数超过 1000 次。");
                recommendations.add("结合电池健康度评估更换电池的时间。");
            } else if (value > 500) {
                deductions += 8;
            }
        }

        if (observedMetrics == 0) {
            return null;
        }
        return BigDecimal.valueOf(Math.max(0, 100 - deductions)).setScale(2, RoundingMode.HALF_UP);
    }

    private String ratingFor(BigDecimal score) {
        if (score == null) return "数据不足";
        double value = score.doubleValue();
        if (value >= 85) return "良好";
        if (value >= 70) return "可接受";
        if (value >= 50) return "需要关注";
        return "需要维护";
    }

    private String summaryFor(Device device, BigDecimal score, DeviceHealth health, int eventCount) {
        String base = device.getDeviceName() + " 已保存 " + eventCount + " 条生命周期事件。";
        if (health == null || score == null) {
            return base + " 当前缺少可评分的健康采集数据。";
        }
        return base + " 最近一次健康采集评分为 " + score.stripTrailingZeros().toPlainString()
                + "，等级为“" + ratingFor(score) + "”。";
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
