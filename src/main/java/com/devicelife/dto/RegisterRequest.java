package com.devicelife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message="사용자 이름을 입력하세요.")
    @Size(min=3,max=50,message="사용자 이름은 3~50자 사이여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min=6,max=100,message = "비밀번호는 6~100자 사이여야 합니다.")
    private String password;

    @Size(max=50)
    private String name;

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}
