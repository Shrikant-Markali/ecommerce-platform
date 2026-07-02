package com.ecommerce.product.service;

import com.ecommerce.product.dto.request.CreateProductRequest;
import com.ecommerce.product.dto.request.UpdateProductRequest;
import com.ecommerce.product.dto.response.ProductResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getAllProducts();

    List<ProductResponse> getProductsByCategory(Long categoryId);

    List<ProductResponse> searchProducts(String keyword);

    List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    List<ProductResponse> getProductsByCategoryAndPriceRange(
            Long categoryId, BigDecimal minPrice, BigDecimal maxPrice);
}