package com.ecommerce.order.service;

import com.ecommerce.order.dto.request.AddToCartRequest;
import com.ecommerce.order.dto.request.UpdateCartItemRequest;
import com.ecommerce.order.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(String email);

    CartResponse addToCart(String email, AddToCartRequest request);

    CartResponse updateCartItem(String email, Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeFromCart(String email, Long cartItemId);

    void clearCart(String email);
}