package com.ecommerce.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Extract JWT token from request header
        String jwt = extractJwtFromRequest(request);

        // Step 2: If no token found, skip to next filter
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Step 3: Extract email from token
            String email = jwtUtil.extractEmail(jwt);

            // Step 4: If email found and no authentication set yet
            if (StringUtils.hasText(email) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // Step 5: Load user from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Step 6: Validate token against user details
                if (jwtUtil.validateToken(jwt, userDetails)) {

                    // Step 7: Create authentication object
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Step 8: Add request details to auth object
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Step 9: Set authentication in Security Context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token invalid — just continue without setting authentication
            // Request will be rejected by Spring Security if endpoint requires auth
        }

        // Step 10: Continue to next filter
        filterChain.doFilter(request, response);
    }

    // Extract JWT from Authorization header
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}