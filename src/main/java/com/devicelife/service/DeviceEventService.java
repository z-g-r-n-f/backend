package com.devicelife.service;

import com.devicelife.dto.DeviceEventRequest;
import com.devicelife.entity.DeviceEvent;
import com.devicelife.exception.BusinessException;
import com.devicelife.exception.ResourceNotFoundException;
import com.devicelife.repository.DeviceEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceEventService {
    private final DeviceEventRepository eventRepository;

    public  List<DeviceEvent> getEventsByDevice(Long deviceId) {
        return eventRepository.findByDeviceIdOrderByEventDateDesc(deviceId);
    }

    public List<DeviceEvent> getRepairHistory(Long deviceId) {
        return eventRepository.findByDeviceIdAndEventType(deviceId, "REPAIR");
    }

    public List<DeviceEvent> getUpgradeHistory(Long deviceId) {
        return eventRepository.findByDeviceIdAndEventType(deviceId, "UPGRADE");
    }

    public List<DeviceEvent> getMaintenanceHistory(Long deviceId) {
        return eventRepository.findByDeviceIdAndEventType(deviceId, "MAINTENANCE");
    }

    public BigDecimal getTotalCost(Long deviceId) {
        return eventRepository.findByDeviceIdOrderByEventDateDesc(deviceId).stream()
                .map(e -> e.getCost() == null ? BigDecimal.ZERO : e.getCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public DeviceEvent addRepair(Long deviceId, DeviceEventRequest r) {
        return addTyped(deviceId, r, "REPAIR");
    }

    @Transactional
    public DeviceEvent addUpgrade(Long deviceId, DeviceEventRequest r) {
        return addTyped(deviceId, r, "UPGRADE");
    }

    @Transactional
    public DeviceEvent addMaintenance(Long deviceId, DeviceEventRequest r) {
        return addTyped(deviceId, r, "MAINTENANCE");
    }

    @Transactional
    public DeviceEvent createEvent(Long deviceId, DeviceEventRequest r) {
        if (r.getEventType() == null) {
            throw new BusinessException("이벤트 유형이 필요합니다.");
        }
        DeviceEvent e = toEntity(r);
        e.setDeviceId(deviceId);
        return eventRepository.save(e);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("이벤트가 존재하지 않습니다. id=" + eventId);
        }
        eventRepository.deleteById(eventId);
    }

    private DeviceEvent addTyped(Long deviceId, DeviceEventRequest r, String type) {
        DeviceEvent e = toEntity(r);
        e.setDeviceId(deviceId);
        e.setEventType(type);
        return eventRepository.save(e);
    }

    private DeviceEvent toEntity(DeviceEventRequest r) {
        DeviceEvent e = new DeviceEvent();
        e.setEventType(r.getEventType());
        e.setTitle(r.getTitle());
        e.setDescription(r.getDescription());
        e.setEventDate(r.getEventDate());
        e.setCost(r.getCost());
        return e;
    }
}
