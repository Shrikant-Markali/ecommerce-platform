package com.ecommerce.order.controller;

import com.ecommerce.order.dto.request.AddToCartRequest;
import com.ecommerce.order.dto.request.UpdateCartItemRequest;
import com.ecommerce.order.dto.response.ApiResponse;
import com.ecommerce.order.dto.response.CartResponse;
import com.ecommerce.order.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            Authentication authentication) {
        String email = authentication.getName();
        CartResponse cart = cartService.getCart(email);
        return ResponseEntity.ok(
                ApiResponse.success("Cart fetched successfully", cart));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request) {
        String email = authentication.getName();
        CartResponse cart = cartService.addToCart(email, request);
        return ResponseEntity.ok(
                ApiResponse.success("Item added to cart successfully", cart));
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        String email = authentication.getName();
        CartResponse cart = cartService.updateCartItem(email, cartItemId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Cart item updated successfully", cart));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(
            Authentication authentication,
            @PathVariable Long cartItemId) {
        String email = authentication.getName();
        CartResponse cart = cartService.removeFromCart(email, cartItemId);
        return ResponseEntity.ok(
                ApiResponse.success("Item removed from cart successfully", cart));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            Authentication authentication) {
        String email = authentication.getName();
        cartService.clearCart(email);
        return ResponseEntity.ok(
                ApiResponse.success("Cart cleared successfully"));
    }
}