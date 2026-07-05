package com.ecommerce.payment.service.impl;

import com.ecommerce.payment.client.OrderClient;
import com.ecommerce.payment.client.UserClient;
import com.ecommerce.payment.dto.request.CreatePaymentRequest;
import com.ecommerce.payment.dto.response.ApiResponse;
import com.ecommerce.payment.dto.response.OrderResponse;
import com.ecommerce.payment.dto.response.PayPalOrderResponse;
import com.ecommerce.payment.dto.response.PaymentResponse;
import com.ecommerce.payment.dto.response.UserResponse;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.exception.PaymentProcessingException;
import com.ecommerce.payment.exception.ResourceNotFoundException;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.service.PaymentService;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final UserClient userClient;
    private final APIContext apiContext;

    @Value("${paypal.success-url}")
    private String successUrl;

    @Value("${paypal.cancel-url}")
    private String cancelUrl;

    @Override
    @Transactional
    public PayPalOrderResponse createPayment(String email, CreatePaymentRequest request) {

        // Step 1: Get user details
        ApiResponse<UserResponse> userApiResponse = userClient.getUserByEmail(email);
        if (!userApiResponse.isSuccess() || userApiResponse.getData() == null) {
            throw new ResourceNotFoundException("User", "email", email);
        }
        UserResponse user = userApiResponse.getData();

        // Step 2: Get order details
        ApiResponse<OrderResponse> orderApiResponse =
                orderClient.getOrderById(request.getOrderId());
        if (!orderApiResponse.isSuccess() || orderApiResponse.getData() == null) {
            throw new ResourceNotFoundException("Order", "id", request.getOrderId());
        }
        OrderResponse order = orderApiResponse.getData();

        // Step 3: Verify order belongs to user
        if (!order.getUserId().equals(user.getId())) {
            throw new PaymentProcessingException("Order does not belong to this user");
        }

        // Step 4: Check order status
        if (!order.getStatus().equals("PENDING")) {
            throw new PaymentProcessingException("Order is not in PENDING status");
        }

        // Step 5: Convert amount to USD (PayPal works in USD)
        // Note: In real app, use currency conversion API
        BigDecimal amountInUsd = order.getTotalAmount()
                .divide(BigDecimal.valueOf(83), 2, RoundingMode.HALF_UP);

        try {
            // Step 6: Create PayPal payment
            com.paypal.api.payments.Payment paypalPayment = createPayPalPayment(
                    amountInUsd,
                    "USD",
                    "Order #" + order.getId(),
                    successUrl + "?orderId=" + order.getId(),
                    cancelUrl + "?orderId=" + order.getId()
            );

            // Step 7: Get approval URL from PayPal response
            String approvalUrl = paypalPayment.getLinks().stream()
                    .filter(link -> link.getRel().equals("approval_url"))
                    .findFirst()
                    .map(Links::getHref)
                    .orElseThrow(() -> new PaymentProcessingException("Could not get PayPal approval URL"));

            // Step 8: Save payment record in DB
            Payment payment = Payment.builder()
                    .orderId(order.getId())
                    .userId(user.getId())
                    .paypalOrderId(paypalPayment.getId())
                    .approvalUrl(approvalUrl)
                    .amount(amountInUsd)
                    .currency("USD")
                    .status(Payment.PaymentStatus.PENDING)
                    .build();

            paymentRepository.save(payment);

            // Step 9: Return approval URL to frontend
            return PayPalOrderResponse.builder()
                    .paypalOrderId(paypalPayment.getId())
                    .approvalUrl(approvalUrl)
                    .status("CREATED")
                    .build();

        } catch (PayPalRESTException e) {
            log.error("PayPal error: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to create PayPal payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponse capturePayment(String paypalOrderId, String payerId) {

        // Step 1: Find payment in DB
        Payment payment = paymentRepository.findByPaypalOrderId(paypalOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment", "paypalOrderId", paypalOrderId));

        try {
            // Step 2: Execute PayPal payment
            com.paypal.api.payments.Payment paypalPayment =
                    new com.paypal.api.payments.Payment();
            paypalPayment.setId(paypalOrderId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            com.paypal.api.payments.Payment executedPayment =
                    paypalPayment.execute(apiContext, paymentExecution);

            // Step 3: Check payment state
            if (executedPayment.getState().equals("approved")) {
                // Step 4: Update payment status
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setPaypalPaymentId(executedPayment.getId());
                paymentRepository.save(payment);

                // Step 5: Update order status in Order Service
                orderClient.updateOrderPayment(
                        payment.getOrderId(),
                        executedPayment.getId(),
                        "COMPLETED"
                );

                log.info("Payment completed for order: {}", payment.getOrderId());
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason("Payment not approved by PayPal");
                paymentRepository.save(payment);

                orderClient.updateOrderPayment(
                        payment.getOrderId(),
                        null,
                        "FAILED"
                );
            }

        } catch (PayPalRESTException e) {
            log.error("PayPal capture error: {}", e.getMessage());
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            throw new PaymentProcessingException("Failed to capture payment: " + e.getMessage());
        }

        return mapToPaymentResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse cancelPayment(String paypalOrderId) {

        Payment payment = paymentRepository.findByPaypalOrderId(paypalOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment", "paypalOrderId", paypalOrderId));

        payment.setStatus(Payment.PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        // Update order status
        orderClient.updateOrderPayment(
                payment.getOrderId(),
                null,
                "CANCELLED"
        );

        return mapToPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment", "orderId", orderId));
        return mapToPaymentResponse(payment);
    }

    @Override
    public List<PaymentResponse> getMyPayments(String email) {
        ApiResponse<UserResponse> userApiResponse = userClient.getUserByEmail(email);
        if (!userApiResponse.isSuccess() || userApiResponse.getData() == null) {
            throw new ResourceNotFoundException("User", "email", email);
        }
        Long userId = userApiResponse.getData().getId();

        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    // ============================================
    // PRIVATE HELPER METHODS
    // ============================================

    private com.paypal.api.payments.Payment createPayPalPayment(
            BigDecimal total,
            String currency,
            String description,
            String successUrl,
            String cancelUrl) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(total.setScale(2, RoundingMode.HALF_UP).toString());

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .paypalOrderId(payment.getPaypalOrderId())
                .paypalPaymentId(payment.getPaypalPaymentId())
                .approvalUrl(payment.getApprovalUrl())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().name())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}