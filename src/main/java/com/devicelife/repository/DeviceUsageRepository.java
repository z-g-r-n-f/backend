package com.devicelife.repository;

import com.devicelife.entity.DeviceUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceUsageRepository extends JpaRepository<DeviceUsage, Long> {
    List<DeviceUsage> findByDeviceIdOrderByUsageDateDesc(Long deviceId);
}