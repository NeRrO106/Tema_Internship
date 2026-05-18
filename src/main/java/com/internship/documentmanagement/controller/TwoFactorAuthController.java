package com.internship.documentmanagement.controller;

import com.internship.documentmanagement.dto.request.Verify2FARequest;
import com.internship.documentmanagement.dto.response.Enable2FAResponse;
import com.internship.documentmanagement.service.TwoFactorAuthService;
import com.internship.documentmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/2fa")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class TwoFactorAuthController {


    private final TwoFactorAuthService twoFactorAuthService;
    private final UserService userService;
    @PostMapping("/setup")
    public ResponseEntity<Enable2FAResponse> setupTwoFactorAuth(Authentication authentication) {
        Long userId = userService.getUserIdByEmail(authentication.getName());
        Enable2FAResponse response = twoFactorAuthService.generateTOTPSecret(userId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/verify")
    public ResponseEntity<String> verifyTwoFactorAuth(
            @RequestBody Verify2FARequest request,
            Authentication authentication) {

        Long userId = userService.getUserIdByEmail(authentication.getName());
        twoFactorAuthService.verifyAndEnable2FA(userId, request);

        return ResponseEntity.ok("2FA successfully enabled!");
    }
    @PostMapping("/disable")
    public ResponseEntity<String> disableTwoFactorAuth(Authentication authentication) {
        Long userId = userService.getUserIdByEmail(authentication.getName());
        twoFactorAuthService.disable2FA(userId);

        return ResponseEntity.ok("2FA successfully disabled!");
    }
    @GetMapping("/status")
    public ResponseEntity<Boolean> getTwoFactorStatus(Authentication authentication) {
        Long userId = userService.getUserIdByEmail(authentication.getName());
        return ResponseEntity.ok(true);
    }

}
