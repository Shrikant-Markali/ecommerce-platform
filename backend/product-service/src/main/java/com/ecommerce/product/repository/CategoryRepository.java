package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find category by name
    Optional<Category> findByNameAndIsDeletedFalse(String name);

    // Check if category name exists
    boolean existsByName(String name);

    // Get all active categories
    List<Category> findByIsDeletedFalseAndIsActiveTrue();

    // Find by id and not deleted
    Optional<Category> findByIdAndIsDeletedFalse(Long id);
}