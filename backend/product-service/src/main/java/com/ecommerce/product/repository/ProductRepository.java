package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find product by id and not deleted
    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    // Get all active non-deleted products
    List<Product> findByIsDeletedFalseAndIsActiveTrue();

    // Get all products by category
    List<Product> findByCategoryIdAndIsDeletedFalseAndIsActiveTrue(Long categoryId);

    // Search products by keyword in name or description
    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.isDeleted = false AND p.isActive = true")
    List<Product> searchProducts(@Param("keyword") String keyword);

    // Filter products by price range
    @Query("SELECT p FROM Product p WHERE " +
            "p.price BETWEEN :minPrice AND :maxPrice " +
            "AND p.isDeleted = false AND p.isActive = true")
    List<Product> findByPriceRange(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);

    // Filter by category and price range
    @Query("SELECT p FROM Product p WHERE " +
            "p.category.id = :categoryId " +
            "AND p.price BETWEEN :minPrice AND :maxPrice " +
            "AND p.isDeleted = false AND p.isActive = true")
    List<Product> findByCategoryAndPriceRange(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);

    // Check if product exists by name in same category
    boolean existsByNameAndCategoryId(String name, Long categoryId);
}