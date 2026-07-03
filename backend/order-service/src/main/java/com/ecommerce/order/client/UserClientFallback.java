package com.ecommerce.order.client;

import com.ecommerce.order.dto.response.ApiResponse;
import com.ecommerce.order.dto.response.UserResponse;
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