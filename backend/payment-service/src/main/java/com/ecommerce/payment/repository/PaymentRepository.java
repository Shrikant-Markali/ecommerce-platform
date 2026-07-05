package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find payment by PayPal order ID
    Optional<Payment> findByPaypalOrderId(String paypalOrderId);

    // Find payment by order ID
    Optional<Payment> findByOrderId(Long orderId);

    // Find all payments by user
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Find all payments for admin
    List<Payment> findAllByOrderByCreatedAtDesc();

    // Find payment by order ID and user ID
    Optional<Payment> findByOrderIdAndUserId(Long orderId, Long userId);
}