package com.internship.documentmanagement.controller;

import com.internship.documentmanagement.dto.request.AdminActionRequest;
import com.internship.documentmanagement.dto.request.ChangeRoleRequest;
import com.internship.documentmanagement.dto.request.RegisterRequest;
import com.internship.documentmanagement.dto.response.UserResponse;
import com.internship.documentmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getProfileByEmail(email));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateProfile(Authentication authentication, @Valid @RequestBody RegisterRequest request){
        String email = authentication.getName();
        return ResponseEntity.ok(userService.updateProfileByEmail(email, request));
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeRole(@PathVariable Long id, @Valid @RequestBody ChangeRoleRequest request) {
        userService.changeRole(id, request.getRole(), request.getReason());
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id, @Valid @RequestBody AdminActionRequest request) {
        userService.deactivateUser(id, request.getReason());
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long id, @Valid @RequestBody AdminActionRequest request) {
        userService.activateUser(id, request.getReason());
        return ResponseEntity.noContent().build();
    }
}
