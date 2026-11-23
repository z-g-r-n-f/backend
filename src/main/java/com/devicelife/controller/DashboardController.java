package com.devicelife.controller;

import com.devicelife.dto.ApiResponse;
import com.devicelife.dto.DashboardResponse;
import com.devicelife.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard(@RequestParam Long userId) {
        return ApiResponse.success(dashboardService.getDashboard(userId));
    }
}
