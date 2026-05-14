package com.internship.documentmanagement.service;

import com.internship.documentmanagement.dto.request.RegisterRequest;
import com.internship.documentmanagement.dto.request.LoginRequest;
import com.internship.documentmanagement.dto.response.AuthResponse;
import com.internship.documentmanagement.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getProfile(Long userId);
    UserResponse updateProfile(Long userId, RegisterRequest request);
    List<UserResponse> getAllUsers();
    void changeRole(Long userId, String role);
    void deactivateUser(Long userId);
    void activateUser(Long userId);
    UserResponse getProfileByEmail(String email);
    UserResponse updateProfileByEmail(String email, RegisterRequest request);

}
