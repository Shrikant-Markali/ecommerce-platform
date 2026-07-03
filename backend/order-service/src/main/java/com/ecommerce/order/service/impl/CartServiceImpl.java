package com.ecommerce.order.service.impl;

import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.client.UserClient;
import com.ecommerce.order.dto.request.AddToCartRequest;
import com.ecommerce.order.dto.request.UpdateCartItemRequest;
import com.ecommerce.order.dto.response.*;
import com.ecommerce.order.entity.Cart;
import com.ecommerce.order.entity.CartItem;
import com.ecommerce.order.exception.InsufficientStockException;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.repository.CartRepository;
import com.ecommerce.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final UserClient userClient;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(String email) {
        Cart cart = cartRepository.findByUserId(getUserId(email))
                .orElse(createEmptyCart(getUserId(email)));
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(String email, AddToCartRequest request) {
        Long userId = getUserId(email);

        // Step 1: Fetch product from Product Service
        ApiResponse<ProductResponse> productApiResponse =
                productClient.getProductById(request.getProductId());

        if (!productApiResponse.isSuccess() || productApiResponse.getData() == null) {
            throw new ResourceNotFoundException("Product", "id", request.getProductId());
        }

        ProductResponse product = productApiResponse.getData();

        // Step 2: Check if product is active
        if (!product.getIsActive()) {
            throw new ResourceNotFoundException("Product", "id", request.getProductId());
        }

        // Step 3: Check stock availability
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    product.getName(),
                    product.getStockQuantity(),
                    request.getQuantity());
        }

        // Step 4: Get or create cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .build();
                    return cartRepository.save(newCart);
                });

        // Step 5: Check if product already in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity if already exists
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            // Check stock for new total quantity
            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException(
                        product.getName(),
                        product.getStockQuantity(),
                        newQuantity);
            }
            item.setQuantity(newQuantity);
        } else {
            // Add new item to cart
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .imageUrl(product.getImageUrl())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(String email, Long cartItemId,
                                       UpdateCartItemRequest request) {
        Long userId = getUserId(email);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CartItem", "id", cartItemId));

        item.setQuantity(request.getQuantity());
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(String email, Long cartItemId) {
        Long userId = getUserId(email);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));

        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    @Override
    @Transactional
    public void clearCart(String email) {
        Long userId = getUserId(email);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
        cart.getItems().clear();
        cartRepository.save(cart);
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

    private Cart createEmptyCart(Long userId) {
        return Cart.builder()
                .userId(userId)
                .build();
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = itemResponses.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalAmount(totalAmount)
                .totalItems(totalItems)
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .imageUrl(item.getImageUrl())
                .build();
    }
}