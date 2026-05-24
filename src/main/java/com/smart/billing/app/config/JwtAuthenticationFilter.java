package com.smart.billing.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter for processing JWT tokens in HTTP requests.
 *
 * This filter extends OncePerRequestFilter to ensure it's executed once per request
 * and intercepts incoming HTTP requests to validate JWT tokens. It extracts the JWT
 * from the Authorization header, validates it, and sets up Spring Security authentication
 * context if the token is valid.
 *
 * The filter performs the following operations:
 * - Extracts JWT token from "Authorization: Bearer <token>" header
 * - Validates token signature and expiration
 * - Extracts user email from token claims
 * - Creates Spring Security authentication context
 * - Allows request to continue through the filter chain
 *
 * This filter is essential for stateless authentication in JWT-based systems,
 * enabling secure API access without session management.
 *
 * @author Smart Billing Team
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /**
     * Processes the JWT authentication for each incoming HTTP request.
     *
     * This method is called once per request and performs the following steps:
     * 1. Extracts the Authorization header from the request
     * 2. Validates that it contains a Bearer token
     * 3. Extracts the JWT token and user email from it
     * 4. Checks if the user is not already authenticated
     * 5. Validates the token's signature and expiration
     * 6. Creates a Spring Security authentication token
     * 7. Sets the authentication in the SecurityContext
     * 8. Continues the filter chain execution
     *
     * If the Authorization header is missing or invalid, the request continues
     * without authentication. If the token is invalid or expired, authentication
     * is not set but the request still continues (allowing other authentication methods).
     *
     * @param request the HTTP servlet request being processed
     * @param response the HTTP servlet response
     * @param filterChain the filter chain for continuing request processing
     * @throws ServletException if a servlet error occurs during filtering
     * @throws IOException if an I/O error occurs during filtering
     */
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Get the Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;


        if(request.getRequestURI().contains("/api/v1/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Validate that the header exists and begins with "Bearer"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token (position 7 onwards) and the user's email
        jwt = authHeader.substring(7);

        try {

            userEmail = jwtService.extractEmail(jwt);

            // Check if the user is not already authenticated in the security context
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Check if the token is valid (not expired and with a correct signature)
                if (jwtService.isTokenValid(jwt, userEmail)) {

                    // Create the authentication object for Spring Security
                    // We're using an empty list of authorities for now (you can load roles here)
                    UserDetails userDetails = new User(userEmail, "", Collections.emptyList());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // Enrich authentication with request details (IP, Session)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the user as "Authenticated" in the system
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            // Continue to the next filter in the chain
            filterChain.doFilter(request, response);

        } catch(ExpiredJwtException e) {
            
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = String.format("{\"error\": \"Token expired\", \"message\": \"%s\"}", 
            request.getRequestURI());

            response.getWriter().write(jsonResponse);
            return;

        }


    }

}
