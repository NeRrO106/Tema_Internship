package com.internship.documentmanagement.service.impl;

import com.google.zxing.WriterException;
import com.internship.documentmanagement.dto.request.Verify2FARequest;
import com.internship.documentmanagement.dto.response.Enable2FAResponse;
import com.internship.documentmanagement.model.User;
import com.internship.documentmanagement.repository.UserRepository;
import com.internship.documentmanagement.service.TwoFactorAuthService;
import com.internship.documentmanagement.util.TOTPUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {

    private final UserRepository userRepository;
    private static final String ISSUER = "Document Management";

    private static final Map<Long, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_TIME = 15 * 60 * 1000;

    @Override
    public Enable2FAResponse generateTOTPSecret(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String secret = TOTPUtil.generateSecret();

        try {
            String qrCodeUrl = TOTPUtil.generateQRCodeDataUrl(
                    secret,
                    user.getEmail(),
                    ISSUER
            );


            user.setTotpSecret(secret);
            user.setTwoFactorEnabled(false);
            user.setTwoFactorVerified(false);
            userRepository.save(user);

            return Enable2FAResponse.builder()
                    .secret(secret)
                    .qrCodeUrl(qrCodeUrl)
                    .manualEntryKey(TOTPUtil.getManualEntryKey(secret))
                    .build();

        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage());
        }
    }
    @Override
    public void verifyAndEnable2FA(Long userId, Verify2FARequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String secret = user.getTotpSecret();
        if (secret == null) {
            throw new RuntimeException("2FA not initialized. Generate secret first.");
        }

        if (!TOTPUtil.verifyCode(secret, request.getTotpCode())) {
            throw new RuntimeException("Invalid TOTP code. Please try again.");
        }

        user.setTwoFactorEnabled(true);
        user.setTwoFactorVerified(true);
        user.setTwoFactorSetupAt(LocalDateTime.now());
        userRepository.save(user);
    }
    @Override
    public void disable2FA(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setTotpSecret(null);
        user.setTwoFactorEnabled(false);
        user.setTwoFactorVerified(false);
        user.setTwoFactorSetupAt(null);
        userRepository.save(user);
    }

    @Override
    public boolean verifyTOTPCode(Long userId, String code) {
        if (failedAttempts.getOrDefault(userId, 0) >= MAX_ATTEMPTS) {
            throw new RuntimeException("Too many failed attempts. Try again later.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getTwoFactorEnabled() || user.getTotpSecret() == null) {
            return true;
        }

        if (TOTPUtil.verifyCode(user.getTotpSecret(), code)) {
            failedAttempts.remove(userId);
            return true;
        } else {
            failedAttempts.put(userId, failedAttempts.getOrDefault(userId, 0) + 1);
            return false;
        }
    }
}
