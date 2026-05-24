package com.smart.billing.app.service;

import com.smart.billing.app.dto.LoginRequestDTO;
import com.smart.billing.app.dto.LoginResponseDTO;
import com.smart.billing.app.dto.RegisterRequestDTO;

/**
 * Service interface for authentication operations in the Smart Billing application.
 *
 * This interface defines the contract for user authentication and registration services,
 * providing a clear separation between the service layer and its implementations.
 * It supports secure user login and registration operations with JWT token generation.
 *
 * All methods work with DTOs (Data Transfer Objects) to maintain loose coupling
 * between the service layer and the presentation layer, ensuring clean architecture
 * and testability. The interface emphasizes security through proper validation
 * and secure token generation.
 *
 * @author Smart Billing Team
 * @version 1.0
 * @since 1.0
 */
public interface IAuthService {

    /**
     * Authenticates a user with email and password credentials.
     *
     * This method validates user credentials, updates login tracking information,
     * and returns user information along with a JWT token for authenticated sessions.
     *
     * @param request the LoginRequestDTO containing email and password for authentication
     * @return LoginResponseDTO containing user information and JWT token
     * @throws ResourceNotFoundException if no user is found with the given email
     * @throws BadCredentialsException if the password does not match the stored hash
     */
    LoginResponseDTO login(LoginRequestDTO request);

    /**
     * Registers a new user in the system.
     *
     * This method creates a new user account with secure password hashing,
     * validates email uniqueness, and returns user information along with
     * a JWT token for immediate authenticated access.
     *
     * @param request the RegisterRequestDTO containing user registration information
     * @return LoginResponseDTO containing the registered user information and JWT token
     * @throws RuntimeException if the email address is already registered
     */
    LoginResponseDTO register(RegisterRequestDTO request);

}
