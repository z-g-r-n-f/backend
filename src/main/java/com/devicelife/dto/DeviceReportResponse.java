package com.devicelife.dto;

import com.devicelife.entity.DeviceHealth;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeviceReportResponse {
    private Long deviceId;
    private String deviceName;
    private LocalDateTime generatedAt;
    private String generatedBy;
    private BigDecimal score;
    private String rating;
    private String summary;
    private List<String> findings;
    private List<String> recommendations;
    private DeviceHealth latestHealth;
    private BigDecimal purchasePrice;
    private BigDecimal currentValue;
    private BigDecimal eventCost;
    private BigDecimal totalCost;
    private Integer eventCount;
    private BigDecimal totalUsageHours;
}
