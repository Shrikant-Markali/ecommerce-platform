package com.ecommerce.user.controller;

import com.ecommerce.user.dto.request.UpdateProfileRequest;
import com.ecommerce.user.dto.response.ApiResponse;
import com.ecommerce.user.dto.response.UserResponse;
import com.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ============================================
    // USER ENDPOINTS (own profile)
    // ============================================

    @GetMapping("/api/v1/users/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            Authentication authentication) {

        String email = authentication.getName();
        UserResponse response = userService.getProfile(email);

        return ResponseEntity.ok(
                ApiResponse.success("Profile fetched successfully", response));
    }

    @PutMapping("/api/v1/users/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {

        String email = authentication.getName();
        UserResponse response = userService.updateProfile(email, request);

        return ResponseEntity.ok(
                ApiResponse.success("Profile updated successfully", response));
    }

    // ============================================
    // ADMIN ENDPOINTS
    // ============================================

    @GetMapping("/api/v1/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(
                ApiResponse.success("Users fetched successfully", users));
    }

    @PutMapping("/api/v1/admin/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean isEnabled) {

        UserResponse response = userService.updateUserStatus(id, isEnabled);

        return ResponseEntity.ok(
                ApiResponse.success("User status updated successfully", response));
    }
}