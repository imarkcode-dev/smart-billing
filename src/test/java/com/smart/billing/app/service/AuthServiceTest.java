package com.smart.billing.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.smart.billing.app.config.JwtService;
import com.smart.billing.app.domain.LoginUser;
import com.smart.billing.app.dto.LoginRequestDTO;
import com.smart.billing.app.dto.LoginResponseDTO;
import com.smart.billing.app.dto.RegisterRequestDTO;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.repository.AuthRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceTest Unit Tests")
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private LoginUser user;
    private final String EMAIL = "test@smartbilling.ai";
    private final String PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        user = new LoginUser();
        user.setEmail(EMAIL);
        user.setPasswordHash("encodedPassword");
        user.setUsername("Juan");
        user.setLastName("Perez");
        user.setAuthProvider("LOCAL");
        user.setRoleUser("ROLE_USER");
        user.setLastLogin(LocalDateTime.now());
    }

    // --- LOGIN TESTS ---

    @Test
    @DisplayName("Successful login returns Token")
    void login_Success() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO(EMAIL, PASSWORD);
        given(authRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(PASSWORD, user.getPasswordHash())).willReturn(true);
        given(jwtService.generateToken(any(LoginResponseDTO.class))).willReturn("jwt-token");
        given(authRepository.save(any(LoginUser.class))).willReturn(user);

        // When
        LoginResponseDTO response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals(EMAIL, response.email());
        verify(authRepository).save(user);
    }

    @Test
    @DisplayName("Login fails because user not found")
    void login_UserNotFound_ThrowsException() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO(EMAIL, PASSWORD);
        given(authRepository.findByEmail(EMAIL)).willReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("Login fails due to incorrect password")
    void login_InvalidPassword_ThrowsException() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO(EMAIL, PASSWORD);
        given(authRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(PASSWORD, user.getPasswordHash())).willReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    // --- TESTS TO REGISTER ---

    @Test
    @DisplayName("New user registration successful")
    void register_Success() {
        // Given
        RegisterRequestDTO request = new RegisterRequestDTO(
            EMAIL, PASSWORD, "Juan", "Perez", "LOCAL", "ROLE_USER"
        );
        given(authRepository.findByEmail(EMAIL)).willReturn(Optional.empty());
        given(passwordEncoder.encode(PASSWORD)).willReturn("encodedPassword");
        given(authRepository.save(any(LoginUser.class))).willReturn(user);
        given(jwtService.generateToken(any(LoginResponseDTO.class))).willReturn("jwt-token");

        // When
        LoginResponseDTO response = authService.register(request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        verify(authRepository).save(any(LoginUser.class));
    }

    @Test
    @DisplayName("Registration fails due to existing email address")
    void register_EmailExists_ThrowsException() {
        // Given
        RegisterRequestDTO request = new RegisterRequestDTO(
            EMAIL, PASSWORD, "Juan", "Perez", "LOCAL", "ROLE_USER"
        );
        given(authRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("The email address is already registered.", exception.getMessage());
        verify(authRepository, never()).save(any(LoginUser.class));
    }
}