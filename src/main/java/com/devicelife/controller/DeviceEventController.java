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
    public ApiResponse<List<DeviceEvent>> getEvents(@PathVariable Long deviceId) {
        return ApiResponse.success(eventService.getEventsByDevice(deviceId));
    }

    @GetMapping("/events/repairs")
    public ApiResponse<List<DeviceEvent>> getRepairs(@PathVariable Long deviceId) {
        return ApiResponse.success(eventService.getRepairHistory(deviceId));
    }

    @GetMapping("/events/upgrades")
    public ApiResponse<List<DeviceEvent>> getUpgrades(@PathVariable Long deviceId) {
        return ApiResponse.success(eventService.getUpgradeHistory(deviceId));
    }

    @GetMapping("/events/maintenance")
    public ApiResponse<List<DeviceEvent>> getMaintenance(@PathVariable Long deviceId) {
        return ApiResponse.success(eventService.getMaintenanceHistory(deviceId));
    }

    @GetMapping("/total-cost")
    public ApiResponse<BigDecimal> getTotalCost(@PathVariable Long deviceId) {
        return ApiResponse.success(eventService.getTotalCost(deviceId));
    }

    @PostMapping("/events")
    public ApiResponse<DeviceEvent> createEvent(
            @PathVariable Long deviceId,
            @Valid @RequestBody DeviceEventRequest request) {
        return ApiResponse.success(eventService.createEvent(deviceId, request));
    }

    @PostMapping("/events/repair")
    public ApiResponse<DeviceEvent> addRepair(
            @PathVariable Long deviceId,
            @Valid @RequestBody DeviceEventRequest request) {
        return ApiResponse.success(eventService.addRepair(deviceId, request));
    }

    @PostMapping("/events/upgrade")
    public ApiResponse<DeviceEvent> addUpgrade(
            @PathVariable Long deviceId,
            @Valid @RequestBody DeviceEventRequest request) {
        return ApiResponse.success(eventService.addUpgrade(deviceId, request));
    }

    @PostMapping("/events/maintenance")
    public ApiResponse<DeviceEvent> addMaintenance(
            @PathVariable Long deviceId,
            @Valid @RequestBody DeviceEventRequest request) {
        return ApiResponse.success(eventService.addMaintenance(deviceId, request));
    }

    @DeleteMapping("/events/{eventId}")
    public ApiResponse<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ApiResponse.success(null);
    }
}
