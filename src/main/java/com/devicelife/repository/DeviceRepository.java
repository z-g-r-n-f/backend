package com.devicelife.repository;


import com.devicelife.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long>{
    List<Device> findByUserId(Long userId);
    List<Device> findByUserIdAndStatus(Long userId, String status);
    Optional<Device> findByDeviceIdAndUserId(Long deviceId, Long userId);

}
