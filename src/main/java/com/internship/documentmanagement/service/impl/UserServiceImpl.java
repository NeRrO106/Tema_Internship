package com.internship.documentmanagement.service.impl;

import com.internship.documentmanagement.dto.request.LoginRequest;
import com.internship.documentmanagement.dto.request.RegisterRequest;
import com.internship.documentmanagement.dto.response.AuthResponse;
import com.internship.documentmanagement.dto.response.UserResponse;
import com.internship.documentmanagement.model.Role;
import com.internship.documentmanagement.model.User;
import com.internship.documentmanagement.repository.UserRepository;
import com.internship.documentmanagement.security.JwtService;
import com.internship.documentmanagement.service.AuditLogService;
import com.internship.documentmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse updateProfile(Long userId, RegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        auditLogService.logAction(userId, "PROFILE_UPDATE", "USER", userId, "Profile updated by user");
        return mapToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    @Override
    public void changeRole(Long userId, String role, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String oldRole = user.getRole().name();
        user.setRole(Role.valueOf(role.toUpperCase()));
        userRepository.save(user);
        auditLogService.logAction(null, "ROLE_CHANGE", "USER", userId,
                String.format("Role changed from %s to %s. Reason: %s", oldRole, role, reason));
    }

    @Override
    public void deactivateUser(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
        auditLogService.logAction(null, "USER_DEACTIVATE", "USER", userId, "Reason: " + reason);
    }
    @Override
    public void activateUser(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
        auditLogService.logAction(null, "USER_ACTIVATE", "USER", userId, "Reason: " + reason);
    }

    @Override
    public UserResponse getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }

    @Override
    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserResponse updateProfileByEmail(String email, RegisterRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        auditLogService.logAction(user.getId(), "PROFILE_UPDATE", "USER", user.getId(), "Profile updated via email endpoint");
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
