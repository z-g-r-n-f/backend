package com.devicelife.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="device_health")
public class DeviceHealth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_id")
    private Long healthId;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "cpu_usage", precision = 5, scale = 2)
    private BigDecimal cpuUsage;

    @Column(name = "memory_usage", precision = 5, scale = 2)
    private BigDecimal memoryUsage;

    @Column(name = "disk_usage", precision = 5, scale = 2)
    private BigDecimal diskUsage;

    @Column(name = "cpu_temperature", precision = 5, scale = 2)
    private BigDecimal cpuTemperature;

    @Column(name = "battery_health", precision = 5, scale = 2)
    private BigDecimal batteryHealth;

    @Column(name = "battery_cycle_count")
    private Integer batteryCycleCount;

    @Column(name = "health_score", precision = 5, scale = 2)
    private BigDecimal healthScore;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @PrePersist
    public void prePersist() {
        if (checkedAt == null) checkedAt = LocalDateTime.now();
    }
}
