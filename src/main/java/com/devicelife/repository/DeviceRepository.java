package com.devicelife.repository;


import com.devicelife.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long>{
    List<Device> findByUserID(Long userId);
    List<Device> findByUserIdAndStatus(Long userId, String status);

}
