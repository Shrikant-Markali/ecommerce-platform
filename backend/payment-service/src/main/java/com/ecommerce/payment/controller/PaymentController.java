package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.request.CreatePaymentRequest;
import com.ecommerce.payment.dto.response.ApiResponse;
import com.ecommerce.payment.dto.response.PayPalOrderResponse;
import com.ecommerce.payment.dto.response.PaymentResponse;
import com.ecommerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ============================================
    // USER ENDPOINTS
    // ============================================

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PayPalOrderResponse>> createPayment(
            Authentication authentication,
            @Valid @RequestBody CreatePaymentRequest request) {
        String email = authentication.getName();
        PayPalOrderResponse response = paymentService.createPayment(email, request);
        return ResponseEntity.ok(
                ApiResponse.success("Payment initiated successfully", response));
    }

    // PayPal redirects here after successful payment
    // Must be PUBLIC (no JWT from PayPal redirect)
    @GetMapping("/success")
    public ResponseEntity<ApiResponse<PaymentResponse>> paymentSuccess(
            @RequestParam("paymentId") String paypalOrderId,
            @RequestParam("PayerID") String payerId) {
        PaymentResponse response = paymentService.capturePayment(paypalOrderId, payerId);
        return ResponseEntity.ok(
                ApiResponse.success("Payment completed successfully", response));
    }

    // PayPal redirects here if customer cancels
    // Must be PUBLIC (no JWT from PayPal redirect)
    @GetMapping("/cancel")
    public ResponseEntity<ApiResponse<PaymentResponse>> paymentCancel(
            @RequestParam("paymentId") String paypalOrderId) {
        PaymentResponse response = paymentService.cancelPayment(paypalOrderId);
        return ResponseEntity.ok(
                ApiResponse.success("Payment cancelled", response));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(
            @PathVariable Long orderId) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(
                ApiResponse.success("Payment fetched successfully", response));
    }

    @GetMapping("/my-payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyPayments(
            Authentication authentication) {
        String email = authentication.getName();
        List<PaymentResponse> payments = paymentService.getMyPayments(email);
        return ResponseEntity.ok(
                ApiResponse.success("Payments fetched successfully", payments));
    }

    // ============================================
    // ADMIN ENDPOINTS
    // ============================================

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(
                ApiResponse.success("All payments fetched successfully", payments));
    }
}