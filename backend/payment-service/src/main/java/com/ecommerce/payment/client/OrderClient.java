package com.ecommerce.payment.client;

import com.ecommerce.payment.dto.response.ApiResponse;
import com.ecommerce.payment.dto.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", fallback = OrderClientFallback.class)
public interface OrderClient {

    @GetMapping("/api/v1/internal/orders/{orderId}")
    ApiResponse<OrderResponse> getOrderById(@PathVariable Long orderId);

    @PutMapping("/api/v1/internal/orders/{orderId}/payment")
    ApiResponse<OrderResponse> updateOrderPayment(
            @PathVariable Long orderId,
            @RequestParam String paymentId,
            @RequestParam String status);
}