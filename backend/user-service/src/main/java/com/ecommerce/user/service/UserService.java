package com.ecommerce.user.service;

import com.ecommerce.user.dto.request.UpdateProfileRequest;
import com.ecommerce.user.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getProfile(String email);

    UserResponse updateProfile(String email, UpdateProfileRequest request);

    List<UserResponse> getAllUsers();

    UserResponse updateUserStatus(Long userId, boolean isEnabled);
}