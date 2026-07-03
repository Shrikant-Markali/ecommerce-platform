package com.ecommerce.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
}