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

            // Check if authentication is already set (shouldn't happen, but good practice)
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                // Create an Authentication object
                // We use UsernamePasswordAuthenticationToken as a container for the authenticated user
                // Parameters: principal (userId), credentials (null), authorities (empty list)
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userId,                    // principal: the authenticated user's ID
                        null,                      // credentials: not needed after authentication
                        Collections.emptyList()    // authorities: user's permissions (empty for now)
                    );

                //  Add request details to the authentication object
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set the authentication in Spring Security's SecurityContext
                // This tells Spring Security: "This request is authenticated!"
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