package com.devicelife.repository;

import com.devicelife.entity.DeviceHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DeviceHealthRepository extends JpaRepository<DeviceHealth, Long> {
    Optional<DeviceHealth> findTopByDeviceIdOrderByCheckedAtDesc(Long deviceId);
}
