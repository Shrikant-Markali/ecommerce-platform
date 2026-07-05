package com.ecommerce.payment.client;

import com.ecommerce.payment.dto.response.ApiResponse;
import com.ecommerce.payment.dto.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public ApiResponse<UserResponse> getUserByEmail(String email) {
        return ApiResponse.error(
                "User service is temporarily unavailable",
                "SERVICE_UNAVAILABLE"
        );
    }
}