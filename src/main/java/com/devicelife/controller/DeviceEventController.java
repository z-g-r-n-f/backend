package com.devicelife.controller;

import com.devicelife.dto.ApiResponse;
import com.devicelife.dto.DeviceEventRequest;
import com.devicelife.entity.DeviceEvent;
import com.devicelife.service.DeviceEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/devices/{deviceId}")
@RequiredArgsConstructor
public class DeviceEventController {
    private final DeviceEventService eventService;

    @GetMapping("/events")
    public ApiResponse<List<DeviceEvent>> getEvents(@PathVariable Long deviceId, @RequestParam Long userId) {
        return ApiResponse.success(eventService.getEventsByDevice(deviceId, userId));
    }

    @GetMapping("/events/repairs")
    public ApiResponse<List<DeviceEvent>> getRepairs(@PathVariable Long deviceId, @RequestParam Long userId) {
        return ApiResponse.success(eventService.getRepairHistory(deviceId, userId));
    }

    @GetMapping("/events/upgrades")
    public ApiResponse<List<DeviceEvent>> getUpgrades(@PathVariable Long deviceId, @RequestParam Long userId) {
        return ApiResponse.success(eventService.getUpgradeHistory(deviceId, userId));
    }

    @GetMapping("/events/maintenance")
    public ApiResponse<List<DeviceEvent>> getMaintenance(@PathVariable Long deviceId, @RequestParam Long userId) {
        return ApiResponse.success(eventService.getMaintenanceHistory(deviceId, userId));
    }

    @GetMapping("/total-cost")
    public ApiResponse<BigDecimal> getTotalCost(@PathVariable Long deviceId, @RequestParam Long userId) {
        return ApiResponse.success(eventService.getTotalCost(deviceId, userId));
    }

    @PostMapping("/events")
    public ApiResponse<DeviceEvent> createEvent(
            @PathVariable Long deviceId,
            @RequestParam Long userId,
            @Valid @RequestBody DeviceEventRequest request) {
        return ApiResponse.success(eventService.createEvent(deviceId, userId, request));
    }

    @PostMapping("/events/repair")
    public ApiResponse<DeviceEvent> addRepair(
            @PathVariable Long deviceId,
            @RequestParam Long userId,
            @Valid @RequestBody DeviceEventRequest request) {
        return ApiResponse.success(eventService.addRepair(deviceId, userId, request));
    }

    @PostMapping("/events/upgrade")
    public ApiResponse<DeviceEvent> addUpgrade(
            @PathVariable Long deviceId,
            @RequestParam Long userId,
            @Valid @RequestBody DeviceEventRequest request) {
        return ApiResponse.success(eventService.addUpgrade(deviceId, userId, request));
    }

    @PostMapping("/events/maintenance")
    public ApiResponse<DeviceEvent> addMaintenance(
            @PathVariable Long deviceId,
            @RequestParam Long userId,
            @Valid @RequestBody DeviceEventRequest request) {
        return ApiResponse.success(eventService.addMaintenance(deviceId, userId, request));
    }

    @DeleteMapping("/events/{eventId}")
    public ApiResponse<Void> deleteEvent(@PathVariable Long deviceId, @PathVariable Long eventId,
                                         @RequestParam Long userId) {
        eventService.deleteEvent(deviceId, eventId, userId);
        return ApiResponse.success(null);
    }
}
