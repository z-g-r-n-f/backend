package com.devicelife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DeviceRequest {
    @NotBlank(message = "기기 이름을 입력하세요.")
    @Size(max = 100)
    private String deviceName;

    @NotBlank(message = "기기 유형을 선택하세요.")
    @Pattern(regexp = "LAPTOP|DESKTOP|PHONE|TABLET|MONITOR|OTHER",
            message = "기기 유형이 올바르지 않습니다.")
    private String deviceType;

    @Size(max = 100)
    private String manufacturer;

    @Size(max = 100)
    private String model;

    @Size(max = 100)
    private String serialNumber;

    @Size(max = 100)
    private String operatingSystem;

    @Size(max = 100)
    private String cpu;

    @PositiveOrZero
    private Integer ramGb;

    @PositiveOrZero
    private Integer storageGb;

    @PastOrPresent(message = "구매일은 오늘 이후일 수 없습니다.")
    private LocalDate purchaseDate;

    @PositiveOrZero(message = "구매 가격은 음수일 수 없습니다.")
    private BigDecimal purchasePrice;

    @PositiveOrZero(message = "현재 가치는 음수일 수 없습니다.")
    private BigDecimal currentValue;

    @Pattern(regexp = "ACTIVE|RETIRED|SOLD", message = "상태가 올바르지 않습니다.")
    private String status;
}
