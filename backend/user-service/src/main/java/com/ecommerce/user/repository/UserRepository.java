package com.ecommerce.user.repository;

import com.ecommerce.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email
    // SQL: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Check if email already exists
    // SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);

    // Find user by email and not deleted
    // SQL: SELECT * FROM users WHERE email = ? AND is_deleted = false
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    // Find user by id and not deleted
    // SQL: SELECT * FROM users WHERE id = ? AND is_deleted = false
    Optional<User> findByIdAndIsDeletedFalse(Long id);

    // Custom query to find all active users
    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    java.util.List<User> findAllActiveUsers();
}