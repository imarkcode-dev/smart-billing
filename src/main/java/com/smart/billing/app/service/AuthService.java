package com.smart.billing.app.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smart.billing.app.config.JwtService;
import com.smart.billing.app.domain.LoginUser;
import com.smart.billing.app.dto.LoginRequestDTO;
import com.smart.billing.app.dto.LoginResponseDTO;
import com.smart.billing.app.dto.RegisterRequestDTO;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.repository.AuthRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Service class for handling authentication and user registration operations.
 *
 * This service provides business logic for user authentication, registration,
 * and JWT token generation. It implements the IAuthService interface and handles
 * password encoding, user validation, and secure token creation.
 *
 * Key features:
 * - User authentication with email/password validation
 * - Secure password hashing using BCrypt
 * - JWT token generation for authenticated sessions
 * - User registration with email uniqueness validation
 * - Automatic role and provider assignment with defaults
 * - Last login timestamp tracking
 * - Transactional operations for data consistency
 *
 * @author Smart Billing Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Authenticates a user with email and password credentials.
     *
     * This method validates the user's existence, verifies the password against
     * the stored hash, updates the last login timestamp, and generates a JWT token.
     * All operations are performed within a transaction for data consistency.
     *
     * @param request the LoginRequestDTO containing email and password for authentication
     * @return LoginResponseDTO containing user information and JWT token
     * @throws ResourceNotFoundException if no user is found with the given email
     * @throws BadCredentialsException if the password does not match the stored hash
     */
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        LoginUser user = authRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid credentials.");
        }

        user.setLastLogin(LocalDateTime.now());
        authRepository.save(user);

        return generateLoginResponse(user);
    }

    /**
     * Registers a new user in the system.
     *
     * This method validates that the email is not already registered, creates a new
     * user entity with encrypted password, sets default values for auth provider
     * ("LOCAL") and role ("ROLE_USER") if not provided, and generates a JWT token
     * for immediate authentication after registration.
     *
     * @param request the RegisterRequestDTO containing user registration information
     * @return LoginResponseDTO containing the registered user information and JWT token
     * @throws RuntimeException if the email address is already registered
     */
    @Override
    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO request) {
        if (authRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("The email address is already registered.");
        }

        LoginUser newUser = new LoginUser();
        newUser.setEmail(request.email());
        newUser.setPasswordHash(passwordEncoder.encode(request.password()));
        newUser.setUsername(request.userName());
        newUser.setLastName(request.lastName());
        newUser.setAuthProvider(request.authProvider() != null ? request.authProvider() : "LOCAL");
        newUser.setRoleUser(request.roleUser() != null ? request.roleUser() : "ROLE_USER");
        newUser.setLastLogin(LocalDateTime.now());

        LoginUser savedUser = authRepository.save(newUser);

        return generateLoginResponse(savedUser);
    }

    /**
     * Generates a login response DTO with JWT token for a given user.
     *
     * This private method creates a LoginResponseDTO from a LoginUser entity,
     * formats the last login timestamp, and generates a JWT token using the
     * JwtService. The token is included in the final response.
     *
     * @param user the LoginUser entity to generate response for
     * @return LoginResponseDTO containing user information and JWT token
     */
    private LoginResponseDTO generateLoginResponse(LoginUser user) {
        String lastLoginStr = user.getLastLogin().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        LoginResponseDTO response = new LoginResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getLastName(),
                user.getAuthProvider(),
                user.getRoleUser(),
                lastLoginStr,
                null
        );

        String token = jwtService.generateToken(response);

        return new LoginResponseDTO(
                response.id(),
                response.email(),
                response.username(),
                response.lastName(),
                response.authProvider(),
                response.roleUser(),
                response.lastLogin(),
                token
        );
    }


}
