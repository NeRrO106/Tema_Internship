package com.internship.documentmanagement.service.impl;

import com.internship.documentmanagement.dto.request.LoginRequest;
import com.internship.documentmanagement.dto.request.RegisterRequest;
import com.internship.documentmanagement.dto.response.AuthResponse;
import com.internship.documentmanagement.model.Role;
import com.internship.documentmanagement.model.User;
import com.internship.documentmanagement.repository.UserRepository;
import com.internship.documentmanagement.security.JwtService;
import com.internship.documentmanagement.service.AuditLogService;
import com.internship.documentmanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl  implements AuthService {

    private final AuditLogService auditLogService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already in use");
        }
        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isActive(true)
                .twoFactorEnabled(false)
                .build();
        userRepository.save(user);

        auditLogService.logAction(user.getId(), "USER_REGISTER", "USER", user.getId(), "Account created successfully");

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    auditLogService.logAction(null, "LOGIN_FAILED", "USER", null, "Failed login attempt for email: " + request.getEmail());
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            auditLogService.logAction(user.getId(), "LOGIN_FAILED", "USER", user.getId(), "Invalid password");
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            auditLogService.logAction(user.getId(), "LOGIN_FAILED", "USER", user.getId(), "Attempted login on deactivated account");
            throw new RuntimeException("Account is deactivated");
        }

        if (user.getTwoFactorEnabled()) {
            auditLogService.logAction(user.getId(), "LOGIN_2FA_PENDING", "USER", user.getId(), "First factor authenticated, awaiting TOTP");

            String tempToken = jwtService.generateToken(user.getEmail(), "TOTP_PENDING");

            return AuthResponse.builder()
                    .username(user.getUsername())
                    .twoFactorRequired(true)
                    .temporaryToken(tempToken)
                    .build();
        }

        auditLogService.logAction(user.getId(), "USER_LOGIN", "USER", user.getId(), "User logged in successfully");

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .twoFactorEnabled(false)
                .twoFactorRequired(false)
                .build();
    }

}
