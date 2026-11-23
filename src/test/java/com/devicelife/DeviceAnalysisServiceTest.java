package com.devicelife;

import com.devicelife.entity.Device;
import com.devicelife.entity.DeviceHealth;
import com.devicelife.repository.DeviceEventRepository;
import com.devicelife.repository.DeviceHealthRepository;
import com.devicelife.repository.DeviceUsageRepository;
import com.devicelife.service.DeviceAnalysisService;
import com.devicelife.service.DeviceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeviceAnalysisServiceTest {
    @Test
    void computesScoreFromStoredHealthMetricsWithoutExternalApi() {
        DeviceService deviceService = mock(DeviceService.class);
        DeviceEventRepository eventRepository = mock(DeviceEventRepository.class);
        DeviceHealthRepository healthRepository = mock(DeviceHealthRepository.class);
        DeviceUsageRepository usageRepository = mock(DeviceUsageRepository.class);

        Device device = new Device();
        device.setDeviceId(4L);
        device.setDeviceName("Test device");
        device.setDeviceType("DESKTOP");
        DeviceHealth health = new DeviceHealth();
        health.setCpuUsage(BigDecimal.valueOf(95));
        health.setMemoryUsage(BigDecimal.valueOf(90));
        health.setDiskUsage(BigDecimal.valueOf(20));

        when(deviceService.getDeviceForUser(4L, 12L)).thenReturn(device);
        when(healthRepository.findTopByDeviceIdOrderByCheckedAtDesc(4L)).thenReturn(Optional.of(health));
        when(eventRepository.findByDeviceIdOrderByEventDateDesc(4L)).thenReturn(List.of());
        when(usageRepository.findByDeviceIdOrderByUsageDateDesc(4L)).thenReturn(List.of());

        DeviceAnalysisService service = new DeviceAnalysisService(
                deviceService, eventRepository, healthRepository, usageRepository, new ObjectMapper());
        var report = service.analyze(4L, 12L);

        assertEquals(BigDecimal.valueOf(60).setScale(2), report.getScore());
        assertEquals("需要关注", report.getRating());
        assertEquals("local-rules", report.getGeneratedBy());
    }
}
