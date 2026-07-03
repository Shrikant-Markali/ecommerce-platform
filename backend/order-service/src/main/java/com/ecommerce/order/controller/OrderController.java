package com.ecommerce.order.controller;

import com.ecommerce.order.dto.request.PlaceOrderRequest;
import com.ecommerce.order.dto.response.ApiResponse;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ============================================
    // USER ENDPOINTS
    // ============================================

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            Authentication authentication,
            @Valid @RequestBody PlaceOrderRequest request) {
        String email = authentication.getName();
        OrderResponse order = orderService.placeOrder(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            Authentication authentication) {
        String email = authentication.getName();
        List<OrderResponse> orders = orderService.getMyOrders(email);
        return ResponseEntity.ok(
                ApiResponse.success("Orders fetched successfully", orders));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            Authentication authentication,
            @PathVariable Long orderId) {
        String email = authentication.getName();
        OrderResponse order = orderService.getOrderById(email, orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Order fetched successfully", order));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            Authentication authentication,
            @PathVariable Long orderId) {
        String email = authentication.getName();
        OrderResponse order = orderService.cancelOrder(email, orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Order cancelled successfully", order));
    }

    // ============================================
    // ADMIN ENDPOINTS
    // ============================================

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(
                ApiResponse.success("All orders fetched successfully", orders));
    }

    @PutMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Order.OrderStatus status) {
        OrderResponse order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(
                ApiResponse.success("Order status updated successfully", order));
    }
}