package com.devicelife.controller;


import com.devicelife.dto.ApiResponse;
import com.devicelife.dto.LoginRequest;
import com.devicelife.dto.RegisterRequest;
import com.devicelife.entity.User;
import com.devicelife.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.devicelife.dto.UpdateUserRequest;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest request){
        return ApiResponse.success(userService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<User> login(@Valid @RequestBody LoginRequest request){
        return ApiResponse.success(userService.login(request));
    }

    @GetMapping("/{userId}")
    public ApiResponse<User> getById(@PathVariable Long userId) {
        return ApiResponse.success(userService.getById(userId));
    }

    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.success(userService.updateUser(userId, request));
    }

}
