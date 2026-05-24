package com.smart.billing.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.billing.app.dto.LoginRequestDTO;
import com.smart.billing.app.dto.LoginResponseDTO;
import com.smart.billing.app.dto.RegisterRequestDTO;
import com.smart.billing.app.service.IAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for handling authentication operations in the Smart Billing application.
 *
 * This controller provides endpoints for user authentication and registration,
 * including login and user registration functionality. It serves as the entry point
 * for authentication-related operations in the REST API.
 *
 * All endpoints are accessible via the base path "/api/v1/auth" and support
 * cross-origin requests from any origin for flexibility in frontend integration.
 *
 * Key features:
 * - User login with email and password validation
 * - User registration with input validation
 * - JWT token generation and response
 * - Comprehensive error handling through global exception handler
 *
 * @author Smart Billing Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final IAuthService authService;

    /**
     * Authenticates a user with email and password credentials.
     *
     * This endpoint validates the user's credentials and returns a JWT token
     * along with user information if authentication is successful. The user's
     * last login timestamp is automatically updated upon successful login.
     *
     * @param request the LoginRequestDTO containing email and password for authentication
     * @return ResponseEntity containing LoginResponseDTO with user info and JWT token
     *         with HTTP 200 OK status on success
     * @throws ConstraintViolationException if request validation fails (missing email/password)
     * @throws BadCredentialsException if credentials are invalid (wrong password)
     * @throws ResourceNotFoundException if user with given email doesn't exist
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Registers a new user in the system.
     *
     * This endpoint creates a new user account with the provided information.
     * It performs validation to ensure the email is unique and all required
     * fields are provided. Upon successful registration, a JWT token is
     * generated and returned along with the user information.
     *
     * @param request the RegisterRequestDTO containing user registration information
     * @return ResponseEntity containing LoginResponseDTO with user info and JWT token
     *         with HTTP 200 OK status on success
     * @throws ConstraintViolationException if request validation fails (invalid email format, missing fields)
     * @throws RuntimeException if email is already registered
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

}
