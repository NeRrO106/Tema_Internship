package com.internship.documentmanagement.controller;

import com.internship.documentmanagement.dto.request.LoginRequest;
import com.internship.documentmanagement.dto.request.RegisterRequest;
import com.internship.documentmanagement.dto.request.Verify2FARequest;
import com.internship.documentmanagement.dto.response.AuthResponse;
import com.internship.documentmanagement.model.User;
import com.internship.documentmanagement.security.JwtService;
import com.internship.documentmanagement.service.AuthService;
import com.internship.documentmanagement.service.TwoFactorAuthService;
import com.internship.documentmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final TwoFactorAuthService twoFactorAuthService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("2FA")) {
                return ResponseEntity.status(202).build();
            }
            throw e;
        }
    }
    @PostMapping("/login/verify-2fa")
    public ResponseEntity<AuthResponse> verifyTwoFactorAndLogin(
            @RequestParam String email,
            @RequestBody Verify2FARequest request) {

        User user = userService.getUserByEmail(email);

        if (!twoFactorAuthService.verifyTOTPCode(user.getId(), request.getTotpCode())) {
            throw new RuntimeException("Codul TOTP este invalid sau a expirat.");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .twoFactorEnabled(true)
                .twoFactorRequired(false)
                .build());
    }
}
