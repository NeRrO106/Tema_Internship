package com.internship.documentmanagement.service;

import com.internship.documentmanagement.dto.request.RegisterRequest;
import com.internship.documentmanagement.dto.response.UserResponse;
import com.internship.documentmanagement.model.User;

import java.util.List;

public interface UserService {
    UserResponse getProfile(Long userId);
    UserResponse updateProfile(Long userId, RegisterRequest request);
    List<UserResponse> getAllUsers();
    void changeRole(Long userId, String role, String reason);
    void deactivateUser(Long userId, String reason);
    void activateUser(Long userId, String reason);
    UserResponse getProfileByEmail(String email);

    Long getUserIdByEmail(String email);

    User getUserByEmail(String email);

    UserResponse updateProfileByEmail(String email, RegisterRequest request);

}
