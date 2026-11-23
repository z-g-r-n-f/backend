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
    private final DeviceService deviceService;

    public List<DeviceEvent> getEventsByDevice(Long deviceId, Long userId) {
        deviceService.getDeviceForUser(deviceId, userId);
        return eventRepository.findByDeviceIdOrderByEventDateDesc(deviceId);
    }

    public List<DeviceEvent> getRepairHistory(Long deviceId, Long userId) {
        deviceService.getDeviceForUser(deviceId, userId);
        return eventRepository.findByDeviceIdAndEventType(deviceId, "REPAIR");
    }

    public List<DeviceEvent> getUpgradeHistory(Long deviceId, Long userId) {
        deviceService.getDeviceForUser(deviceId, userId);
        return eventRepository.findByDeviceIdAndEventType(deviceId, "UPGRADE");
    }

    public List<DeviceEvent> getMaintenanceHistory(Long deviceId, Long userId) {
        deviceService.getDeviceForUser(deviceId, userId);
        return eventRepository.findByDeviceIdAndEventType(deviceId, "MAINTENANCE");
    }

    public BigDecimal getTotalCost(Long deviceId, Long userId) {
        return getEventsByDevice(deviceId, userId).stream()
                .map(e -> e.getCost() == null ? BigDecimal.ZERO : e.getCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public DeviceEvent addRepair(Long deviceId, Long userId, DeviceEventRequest r) {
        return addTyped(deviceId, userId, r, "REPAIR");
    }

    @Transactional
    public DeviceEvent addUpgrade(Long deviceId, Long userId, DeviceEventRequest r) {
        return addTyped(deviceId, userId, r, "UPGRADE");
    }

    @Transactional
    public DeviceEvent addMaintenance(Long deviceId, Long userId, DeviceEventRequest r) {
        return addTyped(deviceId, userId, r, "MAINTENANCE");
    }

    @Transactional
    public DeviceEvent createEvent(Long deviceId, Long userId, DeviceEventRequest r) {
        if (r.getEventType() == null) {
            throw new BusinessException("이벤트 유형이 필요합니다.");
        }
        deviceService.getDeviceForUser(deviceId, userId);
        DeviceEvent e = toEntity(r);
        e.setDeviceId(deviceId);
        return eventRepository.save(e);
    }

    @Transactional
    public void deleteEvent(Long deviceId, Long eventId, Long userId) {
        DeviceEvent event = eventRepository.findById(eventId).orElse(null);
        if (event == null || !deviceId.equals(event.getDeviceId())) {
            throw new ResourceNotFoundException("이벤트가 존재하지 않습니다. id=" + eventId);
        }
        deviceService.getDeviceForUser(deviceId, userId);
        eventRepository.deleteById(eventId);
    }

    private DeviceEvent addTyped(Long deviceId, Long userId, DeviceEventRequest r, String type) {
        deviceService.getDeviceForUser(deviceId, userId);
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
