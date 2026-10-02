package com.devicelife.service;

import com.devicelife.dto.LoginRequest;
import com.devicelife.dto.RegisterRequest;
import com.devicelife.entity.User;
import com.devicelife.exception.BusinessException;
import com.devicelife.exception.DuplicateResourceException;
import com.devicelife.exception.ResourceNotFoundException;
import com.devicelife.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("사용자 이름이 이미 존재합니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("이메일이 이미 등록되었습니다.");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("사용자가 존재하지 않습니다."));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException("비밀번호가 틀립니다.");
        }
        return user;
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자가 존재하지 않습니다. id=" + userId));
    }
}