package com.devicelife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    private String name;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100)
    private String email;

    @Size(min = 6, max = 100, message = "비밀번호는 6~100자 사이여야 합니다.")
    private String password;
}
