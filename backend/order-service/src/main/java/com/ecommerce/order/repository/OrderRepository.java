package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders by user
    List<Order> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);

    // Find order by id and user id (security — user can only see own orders)
    Optional<Order> findByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);

    // Find order by id and not deleted
    Optional<Order> findByIdAndIsDeletedFalse(Long id);

    // Find all orders for admin
    List<Order> findByIsDeletedFalseOrderByCreatedAtDesc();
}