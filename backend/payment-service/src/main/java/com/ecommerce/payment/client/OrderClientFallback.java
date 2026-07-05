package com.ecommerce.payment.client;

import com.ecommerce.payment.dto.response.ApiResponse;
import com.ecommerce.payment.dto.response.OrderResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderClientFallback implements OrderClient {

    @Override
    public ApiResponse<OrderResponse> getOrderById(Long orderId) {
        return ApiResponse.error(
                "Order service is temporarily unavailable",
                "SERVICE_UNAVAILABLE"
        );
    }

    @Override
    public ApiResponse<OrderResponse> updateOrderPayment(
            Long orderId, String paymentId, String status) {
        return ApiResponse.error(
                "Order service is temporarily unavailable",
                "SERVICE_UNAVAILABLE"
        );
    }
}