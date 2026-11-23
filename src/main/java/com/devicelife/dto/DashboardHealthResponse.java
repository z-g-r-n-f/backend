package com.devicelife.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DashboardHealthResponse {
    private Long deviceId;
    private String deviceName;
    private BigDecimal healthScore;
    private BigDecimal cpuUsage;
    private BigDecimal memoryUsage;
    private BigDecimal diskUsage;
    private BigDecimal batteryHealth;
    private LocalDateTime checkedAt;
}
