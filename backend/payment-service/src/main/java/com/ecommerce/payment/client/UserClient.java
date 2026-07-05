package com.ecommerce.payment.client;

import com.ecommerce.payment.dto.response.ApiResponse;
import com.ecommerce.payment.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/api/v1/internal/users/by-email")
    ApiResponse<UserResponse> getUserByEmail(@RequestParam String email);
}