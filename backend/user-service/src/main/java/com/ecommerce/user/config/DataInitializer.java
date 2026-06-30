package com.ecommerce.user.config;

import com.ecommerce.user.entity.Role;
import com.ecommerce.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        // Create ROLE_USER if it doesn't exist
        if (!roleRepository.existsByName(Role.RoleName.ROLE_USER)) {
            Role userRole = new Role();
            userRole.setName(Role.RoleName.ROLE_USER);
            roleRepository.save(userRole);
            System.out.println("✅ ROLE_USER created");
        }

        // Create ROLE_ADMIN if it doesn't exist
        if (!roleRepository.existsByName(Role.RoleName.ROLE_ADMIN)) {
            Role adminRole = new Role();
            adminRole.setName(Role.RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);
            System.out.println("✅ ROLE_ADMIN created");
        }
    }
}