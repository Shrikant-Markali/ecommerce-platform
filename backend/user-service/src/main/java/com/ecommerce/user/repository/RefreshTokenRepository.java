package com.ecommerce.user.repository;

import com.ecommerce.user.entity.RefreshToken;
import com.ecommerce.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Find refresh token by token string
    // SQL: SELECT * FROM refresh_tokens WHERE token = ?
    Optional<RefreshToken> findByToken(String token);

    // Delete all refresh tokens for a user (logout)
    // SQL: DELETE FROM refresh_tokens WHERE user_id = ?
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = ?1")
    void deleteByUser(User user);

    // Check if token exists
    // SQL: SELECT COUNT(*) > 0 FROM refresh_tokens WHERE token = ?
    boolean existsByToken(String token);

    // Find token by user
    Optional<RefreshToken> findByUser(User user);
}