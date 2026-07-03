package com.ecommerce.order.client;

import com.ecommerce.order.dto.response.ApiResponse;
import com.ecommerce.order.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {

    @GetMapping("/api/v1/products/{id}")
    ApiResponse<ProductResponse> getProductById(@PathVariable Long id);
}