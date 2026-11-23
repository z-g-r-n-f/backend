package com.devicelife.controller;

import com.devicelife.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ApiResponse<Map<String, String>> root() {
        return ApiResponse.success(Map.of(
                "service", "device-lifecycle",
                "status", "running",
                "apiBase", "/api"
        ));
    }
}
