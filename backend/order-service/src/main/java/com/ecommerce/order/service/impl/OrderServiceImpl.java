package com.ecommerce.order.service.impl;

import com.ecommerce.order.client.UserClient;
import com.ecommerce.order.dto.request.PlaceOrderRequest;
import com.ecommerce.order.dto.response.ApiResponse;
import com.ecommerce.order.dto.response.OrderItemResponse;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.dto.response.UserResponse;
import com.ecommerce.order.entity.Cart;
import com.ecommerce.order.entity.CartItem;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.repository.CartRepository;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.CartService;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final UserClient userClient;

    @Override
    @Transactional
    public OrderResponse placeOrder(String email, PlaceOrderRequest request) {
        Long userId = getUserId(email);

        // Step 1: Get user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart", "userId", userId));

        // Step 2: Check cart is not empty
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order with empty cart");
        }

        // Step 3: Calculate total amount
        BigDecimal totalAmount = cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Step 4: Create order items from cart items
        Order order = Order.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .isDeleted(false)
                .createdBy(email)
                .build();

        // Step 5: Convert cart items to order items
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .productId(cartItem.getProductId())
                        .productName(cartItem.getProductName())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPrice())
                        .imageUrl(cartItem.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        order.setItems(orderItems);

        // Step 6: Save order
        Order savedOrder = orderRepository.save(order);

        // Step 7: Clear cart after order placed
        cartService.clearCart(email);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String email, Long orderId) {
        Long userId = getUserId(email);

        Order order = orderRepository
                .findByIdAndUserIdAndIsDeletedFalse(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email) {
        Long userId = getUserId(email);
        return orderRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String email, Long orderId) {
        Long userId = getUserId(email);

        Order order = orderRepository
                .findByIdAndUserIdAndIsDeletedFalse(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Can only cancel PENDING orders
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    // ============================================
    // PRIVATE HELPER METHODS
    // ============================================

    private Long getUserId(String email) {
        ApiResponse<UserResponse> response = userClient.getUserByEmail(email);
        if (!response.isSuccess() || response.getData() == null) {
            throw new ResourceNotFoundException("User", "email", email);
        }
        return response.getData().getId();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .imageUrl(item.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .shippingAddress(order.getShippingAddress())
                .paymentId(order.getPaymentId())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}