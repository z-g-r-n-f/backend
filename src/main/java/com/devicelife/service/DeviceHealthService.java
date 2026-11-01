package com.devicelife.service;

import com.devicelife.dto.DeviceHealthRequest;
import com.devicelife.entity.DeviceHealth;
import com.devicelife.exception.ResourceNotFoundException;
import com.devicelife.repository.DeviceHealthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DeviceHealthService {
    private final DeviceHealthRepository healthRepository;

    public DeviceHealth getLatest(Long deviceId) {
        return healthRepository.findTopByDeviceIdOrderByCheckedAtDesc(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("건강 기록이 없습니다."));
    }

    @Transactional
    public DeviceHealth saveHealth(Long deviceId, DeviceHealthRequest r) {
        DeviceHealth h = new DeviceHealth();
        h.setDeviceId(deviceId);
        h.setCpuUsage(r.getCpuUsage());
        h.setMemoryUsage(r.getMemoryUsage());
        h.setDiskUsage(r.getDiskUsage());
        h.setCpuTemperature(r.getCpuTemperature());
        h.setBatteryHealth(r.getBatteryHealth());
        h.setBatteryCycleCount(r.getBatteryCycleCount());
        h.setHealthScore(r.getHealthScore() != null ? r.getHealthScore() : calculateHealthScore(r));
        return healthRepository.save(h);
    }

    private BigDecimal calculateHealthScore(DeviceHealthRequest r) {
        double score = 100.0;
        if (r.getCpuTemperature() != null && r.getCpuTemperature().doubleValue() > 70) {
            score -= (r.getCpuTemperature().doubleValue() - 70) * 2;
        }
        if (r.getBatteryHealth() != null) {
            score = Math.min(score, r.getBatteryHealth().doubleValue());
        }
        if (r.getCpuUsage() != null && r.getCpuUsage().doubleValue() > 80) {
            score -= 10;
        }
        return BigDecimal.valueOf(Math.max(score, 0));
    }
}
