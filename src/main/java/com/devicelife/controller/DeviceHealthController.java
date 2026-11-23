package com.devicelife.controller;

import com.devicelife.dto.ApiResponse;
import com.devicelife.dto.DeviceHealthRequest;
import com.devicelife.entity.DeviceHealth;
import com.devicelife.service.DeviceHealthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices/{deviceId}/health")
@RequiredArgsConstructor
public class DeviceHealthController {
    private final DeviceHealthService healthService;

    @GetMapping
    public ApiResponse<DeviceHealth> getLatest(@PathVariable Long deviceId, @RequestParam Long userId) {
        return ApiResponse.success(healthService.getLatest(deviceId, userId));
    }

    @PostMapping
    public ApiResponse<DeviceHealth> upload(
            @PathVariable Long deviceId,
            @RequestParam Long userId,
            @Valid @RequestBody DeviceHealthRequest request) {
        return ApiResponse.success(healthService.saveHealth(deviceId, userId, request));
    }
}
