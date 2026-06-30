package com.ecommerce.user.repository;

import com.ecommerce.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find role by name
    // SQL: SELECT * FROM roles WHERE name = ?
    Optional<Role> findByName(Role.RoleName name);

    // Check if role exists
    // SQL: SELECT COUNT(*) > 0 FROM roles WHERE name = ?
    boolean existsByName(Role.RoleName name);
}