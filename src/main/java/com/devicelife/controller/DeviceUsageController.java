package com.devicelife.controller;

import com.devicelife.dto.ApiResponse;
import com.devicelife.dto.DeviceUsageRequest;
import com.devicelife.entity.DeviceUsage;
import com.devicelife.service.DeviceUsageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices/{deviceId}/usage")
@RequiredArgsConstructor
public class DeviceUsageController {
    private final DeviceUsageService usageService;

    @GetMapping
    public ApiResponse<List<DeviceUsage>> list(@PathVariable Long deviceId) {
        return ApiResponse.success(usageService.getByDevice(deviceId));
    }

    @PostMapping
    public ApiResponse<DeviceUsage> save(
            @PathVariable Long deviceId,
            @Valid @RequestBody DeviceUsageRequest request) {
        return ApiResponse.success(usageService.save(deviceId, request));
    }
}
