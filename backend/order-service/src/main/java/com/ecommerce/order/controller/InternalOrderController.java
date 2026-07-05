package com.ecommerce.order.controller;

import com.ecommerce.order.dto.response.ApiResponse;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/orders")
@RequiredArgsConstructor
public class InternalOrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByIdInternal(
            @PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderByIdInternal(orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Order fetched successfully", order));
    }

    @PutMapping("/{orderId}/payment")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderPayment(
            @PathVariable Long orderId,
            @RequestParam String paymentId,
            @RequestParam String status) {
        OrderResponse order = orderService.updateOrderPayment(orderId, paymentId, status);
        return ResponseEntity.ok(
                ApiResponse.success("Order payment updated successfully", order));
    }
}