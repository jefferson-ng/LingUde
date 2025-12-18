package com.sep.sep_backend.auth.filter;

import com.sep.sep_backend.auth.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;


import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

/**
 *  JWT Authentication Filter
 *
 * <p>
 *     This filter intercepts ALL incoming HTTP requests and checks for a valid JWT token.
 *   If a valid token is found, it authenticates the user in Spring Security's context.
 * </p>
 *
 * Key responsibilities:
 * <ol>
 *     <li>Extract JWT from the Authorization header</li>
 *     <li>Validate the token using JwtUtil</li>
 *     <li>Set authentication in SecurityContext so Spring Security knows the user is authenticated</li>
 *     <li>Continue the filter chain</li>
 * </ol>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Extract the Authorization header
        String authorizationHeader = request.getHeader("Authorization");

        // Check if the header exists and starts with "Bearer "
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // No token provided - let the request continue
            // Spring Security will handle denying access to protected endpoints
            filterChain.doFilter(request, response);
            return;
        }

        try {
            //  Extract the actual token (remove "Bearer" prefix)
            String token = authorizationHeader.substring(7); // "Bearer ".length() == 7

            // Validate the token
            if (!jwtUtil.validateToken(token)) {
                // Invalid token - let the request continue without authentication
                // Spring Security will deny access to protected endpoints
                filterChain.doFilter(request, response);
                return;
            }

            // Extract userId from the valid token
            UUID userId = jwtUtil.getUserIdFromToken(token);

            // Extract role from token (e.g. "USER" or "ADMIN")
            String role = jwtUtil.getRoleFromToken(token);

            // Only set authentication if not already set
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                // Build the list of authorities based on the role from the token.
                //
                // Spring Security expects roles in the form "ROLE_XYZ",
                // so we transform "ADMIN" -> "ROLE_ADMIN", "USER" -> "ROLE_USER".
                List<GrantedAuthority> authorities = null;

                if (role != null && !role.isBlank()) {
                    authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                } else {
                    // Fallback: no role information -> no authorities
                    authorities = List.of();
                }

                // Create an Authentication object that contains:
                // - principal: the authenticated user's ID (UUID)
                // - credentials: null (we don't store passwords here)
                // - authorities: derived from the JWT's role claim
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                authorities
                        );

                // Add request details (IP, session, etc.)
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Store the authentication in the SecurityContext,
                // so that Spring Security knows the user is authenticated
                // AND which authorities (roles) they have.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }


        } catch (Exception e) {
            // If anything goes wrong (invalid token format, expired token, etc.)
            // we just continue without setting authentication
            // Spring Security will deny access to protected endpoints
            System.err.println("JWT Authentication failed: " + e.getMessage());
        }

        // Continue the filter chain (let the request proceed)
        filterChain.doFilter(request, response);
    }
}