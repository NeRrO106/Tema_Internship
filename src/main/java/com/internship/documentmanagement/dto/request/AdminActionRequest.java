package com.internship.documentmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminActionRequest {
    @NotBlank(message = "Reason is required")
    private String reason;
}
