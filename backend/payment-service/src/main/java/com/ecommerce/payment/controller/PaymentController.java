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
    public void paymentSuccess(
            @RequestParam("paymentId") String paypalOrderId,
            @RequestParam("PayerID") String payerId,
            jakarta.servlet.http.HttpServletResponse httpResponse) throws java.io.IOException {

        try {
            PaymentResponse response = paymentService.capturePayment(paypalOrderId, payerId);
            if (response.getStatus().equals("COMPLETED")) {
                httpResponse.sendRedirect("http://localhost:5173/payment/success");
            } else {
                httpResponse.sendRedirect("http://localhost:5173/payment/cancel");
            }
        } catch (Exception e) {
            httpResponse.sendRedirect("http://localhost:5173/payment/cancel");
        }
    }

    @GetMapping("/cancel")
    public void paymentCancel(
            @RequestParam(value = "paymentId", required = false) String paypalOrderId,
            jakarta.servlet.http.HttpServletResponse httpResponse) throws java.io.IOException {

        try {
            if (paypalOrderId != null) {
                paymentService.cancelPayment(paypalOrderId);
            }
        } catch (Exception e) {
            // ignore
        }
        httpResponse.sendRedirect("http://localhost:5173/payment/cancel");
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