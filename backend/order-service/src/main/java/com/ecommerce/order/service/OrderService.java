package com.ecommerce.order.service;

import com.ecommerce.order.dto.request.PlaceOrderRequest;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.entity.Order;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(String email, PlaceOrderRequest request);

    OrderResponse getOrderById(String email, Long orderId);

    List<OrderResponse> getMyOrders(String email);

    OrderResponse cancelOrder(String email, Long orderId);

    // Admin methods
    List<OrderResponse> getAllOrders();

    OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus status);

    OrderResponse updateOrderPayment(Long orderId, String paymentId, String status);

    OrderResponse getOrderByIdInternal(Long orderId);

}