package com.devicelife.controller;

import com.devicelife.dto.ApiResponse;
import com.devicelife.dto.DeviceRequest;
import com.devicelife.entity.Device;
import com.devicelife.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor

public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping
    public ApiResponse<List<Device>> getDevices(@RequestParam Long userId){
        return ApiResponse.success(deviceService.getDevicesByUser(userId));
    }

    @GetMapping("/{deviceId}")
    public ApiResponse<Device> getDevice(@PathVariable Long deviceId) {
        return ApiResponse.success(deviceService.getDevice(deviceId));
    }

    @PostMapping
    public ApiResponse<Device> createDevice(
            @RequestParam Long userId,
            @Valid @RequestBody DeviceRequest request
    ){
        return ApiResponse.success(deviceService.createDevice(userId, request));
    }

    @PutMapping("/{deviceId}")
    public ApiResponse<Device> updateDevice(
            @PathVariable Long deviceId,
            @Valid @RequestBody DeviceRequest request) {
        return ApiResponse.success(deviceService.updateDevice(deviceId, request));
    }

    @DeleteMapping("/{deviceId}")
    public ApiResponse<Void> deleteDevice(@PathVariable Long deviceId) {
        deviceService.deleteDevice(deviceId);
        return ApiResponse.success(null);
    }
}
