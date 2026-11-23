package com.devicelife.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardResponse {
    private int deviceCount;
    private BigDecimal purchaseValue;
    private BigDecimal currentValue;
    private BigDecimal eventCost;
    private BigDecimal totalCost;
    private Map<String, Integer> statusCounts;
    private List<DashboardHealthResponse> health;
}
