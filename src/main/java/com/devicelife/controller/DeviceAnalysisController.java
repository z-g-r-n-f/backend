package com.devicelife.controller;

import com.devicelife.dto.ApiResponse;
import com.devicelife.dto.DeviceReportResponse;
import com.devicelife.service.DeviceAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices/{deviceId}/analysis")
@RequiredArgsConstructor
public class DeviceAnalysisController {
    private final DeviceAnalysisService analysisService;

    @GetMapping
    public ApiResponse<DeviceReportResponse> report(@PathVariable Long deviceId,
                                                     @RequestParam Long userId) {
        return ApiResponse.success(analysisService.analyze(deviceId, userId));
    }
}
