package com.internship.documentmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeRoleRequest {
    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Reason is required")
    private String reason;
}
