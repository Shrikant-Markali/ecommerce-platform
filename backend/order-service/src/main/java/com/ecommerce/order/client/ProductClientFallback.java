package com.ecommerce.order.client;

import com.ecommerce.order.dto.response.ApiResponse;
import com.ecommerce.order.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public ApiResponse<ProductResponse> getProductById(Long id) {
        return ApiResponse.error(
                "Product service is temporarily unavailable",
                "SERVICE_UNAVAILABLE"
        );
    }
}