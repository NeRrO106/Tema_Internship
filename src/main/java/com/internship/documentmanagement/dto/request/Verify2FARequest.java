package com.internship.documentmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Verify2FARequest {
    @NotBlank(message = "TOTP code is required")
    @Pattern(regexp = "\\d{6}", message = "TOTP code must be 6 digits")
    private String totpCode;
}
