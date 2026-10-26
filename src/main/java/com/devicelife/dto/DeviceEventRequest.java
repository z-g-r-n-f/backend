package com.devicelife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DeviceEventRequest {

    @NotBlank(message = "이벤트 유형을 선택하세요.")
    @Pattern(regexp = "PURCHASE|REPAIR|UPGRADE|MAINTENANCE|SALE",
            message = "이벤트 유형이 올바르지 않습니다.")
    private String eventType;

    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "이벤트 날짜를 입력하세요.")
    @PastOrPresent(message = "이벤트 날짜는 오늘 이후일 수 없습니다.")
    private LocalDate eventDate;

    @PositiveOrZero(message = "비용은 음수일 수 없습니다.")
    private BigDecimal cost;
}
