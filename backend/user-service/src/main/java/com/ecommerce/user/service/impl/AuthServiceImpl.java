package com.ecommerce.user.service.impl;

import com.ecommerce.user.dto.request.LoginRequest;
import com.ecommerce.user.dto.request.RegisterRequest;
import com.ecommerce.user.dto.response.AuthResponse;
import com.ecommerce.user.entity.RefreshToken;
import com.ecommerce.user.entity.Role;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.exception.EmailAlreadyExistsException;
import com.ecommerce.user.exception.InvalidTokenException;
import com.ecommerce.user.exception.ResourceNotFoundException;
import com.ecommerce.user.repository.RefreshTokenRepository;
import com.ecommerce.user.repository.RoleRepository;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.CustomUserDetailsService;
import com.ecommerce.user.security.JwtUtil;
import com.ecommerce.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // Step 1: Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // Step 2: Get default ROLE_USER
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_USER"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        // Step 3: Build new User entity
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hash password
                .phone(request.getPhone())
                .isEnabled(true)
                .isDeleted(false)
                .roles(roles)
                .build();

        // Step 4: Save user to database
        User savedUser = userRepository.save(user);

        // Step 5: Generate JWT tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()));
        String accessToken = jwtUtil.generateAccessToken(extraClaims, userDetails);
        String refreshTokenStr = createRefreshToken(savedUser);

        // Step 6: Build and return response
        return buildAuthResponse(savedUser, accessToken, refreshTokenStr);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // Step 1: Authenticate using Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Step 2: Fetch user from database
        User user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // Step 3: Generate JWT tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()));
        String accessToken = jwtUtil.generateAccessToken(extraClaims, userDetails);

        // Step 4: Delete old refresh token (if any) and create new one
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        String refreshTokenStr = createRefreshToken(user);

        // Step 5: Build and return response
        return buildAuthResponse(user, accessToken, refreshTokenStr);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {

        // Step 1: Find refresh token in database
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        // Step 2: Check if expired
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new InvalidTokenException("Refresh token has expired. Please login again");
        }

        // Step 3: Get user from token
        User user = token.getUser();

        // Step 4: Generate new access token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()));
        String newAccessToken = jwtUtil.generateAccessToken(extraClaims, userDetails);

        // Step 5: Return response with SAME refresh token (not creating new one)
        return buildAuthResponse(user, newAccessToken, refreshToken);
    }

    @Override
    @Transactional
    public void logout(String email) {

        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Delete all refresh tokens for this user
        refreshTokenRepository.deleteByUser(user);
    }

    // ============================================
    // PRIVATE HELPER METHODS
    // ============================================

    private String createRefreshToken(User user) {
        String tokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(roleNames)
                .build();
    }
}