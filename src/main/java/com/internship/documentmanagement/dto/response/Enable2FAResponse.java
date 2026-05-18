package com.internship.documentmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enable2FAResponse {
    private String secret;
    private String qrCodeUrl;
    private String manualEntryKey;
    private String[] backupCodes;
}
