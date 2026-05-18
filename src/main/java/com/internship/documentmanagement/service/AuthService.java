package com.internship.documentmanagement.service;

import com.internship.documentmanagement.dto.request.LoginRequest;
import com.internship.documentmanagement.dto.request.RegisterRequest;
import com.internship.documentmanagement.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
