package com.devicelife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DeviceHealthRequest {
    @DecimalMin(value = "0.0") @DecimalMax(value = "100.0")
    private BigDecimal cpuUsage;

    @DecimalMin(value = "0.0") @DecimalMax(value = "100.0")
    private BigDecimal memoryUsage;

    @DecimalMin(value = "0.0") @DecimalMax(value = "100.0")
    private BigDecimal diskUsage;

    @DecimalMin(value = "0.0")
    private BigDecimal cpuTemperature;

    @DecimalMin(value = "0.0") @DecimalMax(value = "100.0")
    private BigDecimal batteryHealth;

    @PositiveOrZero
    private Integer batteryCycleCount;

    @DecimalMin(value = "0.0") @DecimalMax(value = "100.0")
    private BigDecimal healthScore;
}
