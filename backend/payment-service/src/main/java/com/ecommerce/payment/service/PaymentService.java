package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.request.CreatePaymentRequest;
import com.ecommerce.payment.dto.response.PayPalOrderResponse;
import com.ecommerce.payment.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PayPalOrderResponse createPayment(String email, CreatePaymentRequest request);

    PaymentResponse capturePayment(String paypalOrderId, String payerId);

    PaymentResponse cancelPayment(String paypalOrderId);

    PaymentResponse getPaymentByOrderId(Long orderId);

    List<PaymentResponse> getMyPayments(String email);

    List<PaymentResponse> getAllPayments();
}