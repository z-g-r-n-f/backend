package com.devicelife.repository;

import com.devicelife.entity.DeviceEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceEventRepository extends JpaRepository<DeviceEvent,Long> {
    List<DeviceEvent> findByDeviceIdOrderByEventDateDesc(Long deviceId);
    List<DeviceEvent> findByDeviceIdAndEventType(Long deviceId, String eventType);
}
