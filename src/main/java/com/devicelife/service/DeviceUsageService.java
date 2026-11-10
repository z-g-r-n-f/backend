package com.devicelife.service;

import com.devicelife.dto.DeviceUsageRequest;
import com.devicelife.entity.DeviceUsage;
import com.devicelife.repository.DeviceUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceUsageService {
    private final DeviceUsageRepository usageRepository;

    public List<DeviceUsage> getByDevice(Long deviceId) {
        return usageRepository.findByDeviceIdOrderByUsageDateDesc(deviceId);
    }

    @Transactional
    public DeviceUsage save(Long deviceId, DeviceUsageRequest r) {
        DeviceUsage u = new DeviceUsage();
        u.setDeviceId(deviceId);
        u.setUsageDate(r.getUsageDate());
        u.setUsageHours(r.getUsageHours());
        return usageRepository.save(u);
    }
}
