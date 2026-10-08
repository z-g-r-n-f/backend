package com.devicelife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DeviceEventRequest {
    @NotNull(message = "사용 날짜를 입력하세요.")
    @PastOrPresent
    private LocalDate usageDate;

    @PositiveOrZero(message = "사용 시간은 음수일 수 없습니다.")
    private BigDecimal usageHours;
}
