package com.internship.documentmanagement.service;

import com.internship.documentmanagement.dto.request.Verify2FARequest;
import com.internship.documentmanagement.dto.response.Enable2FAResponse;

public interface TwoFactorAuthService {
    Enable2FAResponse generateTOTPSecret(Long userId);
    void verifyAndEnable2FA(Long userId, Verify2FARequest request);
    void disable2FA(Long userId);
    boolean verifyTOTPCode(Long userId, String code);
}
