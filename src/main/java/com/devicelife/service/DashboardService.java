package com.devicelife.service;

import com.devicelife.dto.DashboardHealthResponse;
import com.devicelife.dto.DashboardResponse;
import com.devicelife.entity.Device;
import com.devicelife.entity.DeviceEvent;
import com.devicelife.entity.DeviceHealth;
import com.devicelife.repository.DeviceEventRepository;
import com.devicelife.repository.DeviceHealthRepository;
import com.devicelife.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DeviceRepository deviceRepository;
    private final DeviceEventRepository eventRepository;
    private final DeviceHealthRepository healthRepository;

    public DashboardResponse getDashboard(Long userId) {
        List<Device> devices = deviceRepository.findByUserId(userId);
        BigDecimal purchaseValue = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;
        BigDecimal eventCost = BigDecimal.ZERO;
        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        List<DashboardHealthResponse> healthItems = new ArrayList<>();

        for (Device device : devices) {
            purchaseValue = purchaseValue.add(valueOrZero(device.getPurchasePrice()));
            currentValue = currentValue.add(valueOrZero(device.getCurrentValue()));
            String status = device.getStatus() == null ? "UNKNOWN" : device.getStatus();
            statusCounts.merge(status, 1, Integer::sum);
            for (DeviceEvent event : eventRepository.findByDeviceIdOrderByEventDateDesc(device.getDeviceId())) {
                eventCost = eventCost.add(valueOrZero(event.getCost()));
            }

            DeviceHealth latest = healthRepository.findTopByDeviceIdOrderByCheckedAtDesc(device.getDeviceId()).orElse(null);
            if (latest != null) {
                DashboardHealthResponse item = new DashboardHealthResponse();
                item.setDeviceId(device.getDeviceId());
                item.setDeviceName(device.getDeviceName());
                item.setHealthScore(latest.getHealthScore());
                item.setCpuUsage(latest.getCpuUsage());
                item.setMemoryUsage(latest.getMemoryUsage());
                item.setDiskUsage(latest.getDiskUsage());
                item.setBatteryHealth(latest.getBatteryHealth());
                item.setCheckedAt(latest.getCheckedAt());
                healthItems.add(item);
            }
        }

        DashboardResponse response = new DashboardResponse();
        response.setDeviceCount(devices.size());
        response.setPurchaseValue(purchaseValue);
        response.setCurrentValue(currentValue);
        response.setEventCost(eventCost);
        response.setTotalCost(purchaseValue.add(eventCost));
        response.setStatusCounts(statusCounts);
        response.setHealth(healthItems);
        return response;
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
