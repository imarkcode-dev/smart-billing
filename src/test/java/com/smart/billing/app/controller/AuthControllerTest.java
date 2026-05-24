/*
Generate unit tests for AuthController using JUnit 5 and Mockito.

Requirements:
- Use @ExtendWith(MockitoExtension.class)
- Mock IAuthService
- Use MockMvc (standalone setup)
- Test endpoints:
    - POST /register (success, validation errors, internal error)
    - POST /login (success, validation errors, resource not found, internal error)
- Follow Given-When-Then structure
- Verify service interactions (verify, verifyNoInteractions, verifyNoMoreInteractions)
- Validate HTTP status codes and response body
- Cover edge cases: null, blank, malformed JSON, empty body
- Achieve 100% line and branch coverage
- Do not use @SpringBootTest
*/

package com.smart.billing.app.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.billing.app.dto.LoginRequestDTO;
import com.smart.billing.app.dto.LoginResponseDTO;
import com.smart.billing.app.dto.RegisterRequestDTO;
import com.smart.billing.app.exception.GlobalExceptionHandler;
import com.smart.billing.app.exception.ResourceNotFoundException;
import com.smart.billing.app.service.IAuthService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthControllerTest Unit Tests")
public class AuthControllerTest {

    @Mock
    private IAuthService authService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LoginResponseDTO testLoginResponse;
    private LoginResponseDTO testRegisterResponse;

    @BeforeEach
    void setUp() {
        // Given: Setup MockMvc with standalone configuration
        AuthController authController = new AuthController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        // Setup test response data
        testLoginResponse = new LoginResponseDTO(
                1,
                "test@example.com",
                "testuser",
                "User",
                "LOCAL",
                "ROLE_USER",
                "2026-05-08T10:00:00",
                "jwt-token-123"
        );

        testRegisterResponse = new LoginResponseDTO(
                2,
                "newuser@example.com",
                "newuser",
                "NewUser",
                "LOCAL",
                "ROLE_USER",
                "2026-05-08T10:00:00",
                "jwt-token-456"
        );
    }

    // ===== LOGIN ENDPOINT TESTS =====

    @Test
    void testLogin_Success() throws Exception {
        // Given: Valid login request and service returns successful response
        LoginRequestDTO request = new LoginRequestDTO("test@example.com", "password123");
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(testLoginResponse);

        // When: POST /api/v1/auth/login
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Then: Status is 200 OK and response contains login data
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.authProvider").value("LOCAL"));

        // Verify service was called exactly once
        verify(authService, times(1)).login(any(LoginRequestDTO.class));
        verifyNoMoreInteractions(authService);
    }

    @Test
    void testLogin_MissingEmail() throws Exception {
        // Given: Login request with blank email
        String json = "{\"email\": \"\", \"password\": \"password123\"}";

        // When: POST /api/v1/auth/login with blank email
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    }

    @Test
    void testLogin_MissingPassword() throws Exception {
        // Given: Login request with blank password
        String json = "{\"email\": \"test@example.com\", \"password\": \"\"}";

        // When: POST /api/v1/auth/login with blank password
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    }

    @Test
    void testLogin_NullEmail() throws Exception {
        // Given: Login request with null email
        String json = "{\"password\": \"password123\"}";

        // When: POST /api/v1/auth/login with null email
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        // Given: Valid login request but user does not exist
        LoginRequestDTO request = new LoginRequestDTO("nonexistent@example.com", "password123");
        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("User not found."));

        // When: POST /api/v1/auth/login with non-existent email
        // Then: Status is 404 Not Found
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // Verify service was called once
        verify(authService, times(1)).login(any(LoginRequestDTO.class));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Given: Valid format request but wrong password
        LoginRequestDTO request = new LoginRequestDTO("test@example.com", "wrongPassword");
        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials."));

        // When: POST /api/v1/auth/login with wrong password
        // Then: Status is 401 Unauthorized
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
                

        // Verify service was called once
        verify(authService, times(1)).login(any(LoginRequestDTO.class));
    }

    @Test
    void testLogin_EmptyBody() throws Exception {
        // Given: Empty request body
        // When: POST /api/v1/auth/login with empty body
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    }

    @Test
    void testLogin_MalformedJson() throws Exception {
        // Given: Malformed JSON
        // When: POST /api/v1/auth/login with invalid JSON
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    }

    // ===== REGISTER ENDPOINT TESTS =====

    @Test
    void testRegister_Success() throws Exception {
        // Given: Valid register request and service returns successful response
        RegisterRequestDTO request = new RegisterRequestDTO(
                "newuser@example.com",
                "password123",
                "newuser",
                "NewUser",
                null,
                null
        );
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(testRegisterResponse);

        // When: POST /api/v1/auth/register
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Then: Status is 200 OK and response contains registration data
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.lastName").value("NewUser"))
                .andExpect(jsonPath("$.token").value("jwt-token-456"));

        // Verify service was called exactly once
        verify(authService, times(1)).register(any(RegisterRequestDTO.class));
        verifyNoMoreInteractions(authService);
    }

    @Test
    void testRegister_MissingEmail() throws Exception {
        // Given: Register request with blank email
        String json = "{\"email\": \"\", \"password\": \"password123\", \"userName\": \"user\", \"lastName\": \"User\"}";

        // When: POST /api/v1/auth/register with blank email
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    }

    @Test
    void testRegister_InvalidEmailFormat() throws Exception {
        // Given: Register request with invalid email format
        String json = "{\"email\": \"not-an-email\", \"password\": \"password123\", \"userName\": \"user\", \"lastName\": \"User\"}";

        // When: POST /api/v1/auth/register with invalid email
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    }
    

    

  

    @Test
    void testRegister_MissingPassword() throws Exception {
        // Given: Register request with blank password
        String json = "{\"email\": \"newuser@example.com\", \"password\": \"\", \"userName\": \"user\", \"lastName\": \"User\"}";

        // When: POST /api/v1/auth/register with blank password
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    }

   
    @Test
    void testRegister_MissingUsername() throws Exception {
        // Given: Register request with blank username
        String json = "{\"email\": \"newuser@example.com\", \"password\": \"password123\", \"userName\": \"\", \"lastName\": \"User\"}";

        // When: POST /api/v1/auth/register with blank username
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    }

    
    @Test
    void testRegister_MissingLastName() throws Exception {
        // Given: Register request with blank last name
        String json = "{\"email\": \"newuser@example.com\", \"password\": \"password123\", \"userName\": \"user\", \"lastName\": \"\"}";

        // When: POST /api/v1/auth/register with blank last name
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    }

    
    @Test
    void testRegister_DuplicateEmail() throws Exception {
        // Given: Register request with email that already exists
        RegisterRequestDTO request = new RegisterRequestDTO(
                "existing@example.com",
                "password123",
                "user",
                "User",
                null,
                null
        );
        when(authService.register(any(RegisterRequestDTO.class)))
                .thenThrow(new RuntimeException("The email address is already registered."));

        // When: POST /api/v1/auth/register with duplicate email
        // Then: Status is 500 Internal Server Error
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        // Verify service was called once
        verify(authService, times(1)).register(any(RegisterRequestDTO.class));
    }

    
    @Test
    void testRegister_WithCustomAuthProvider() throws Exception {
        // Given: Register request with custom auth provider
        RegisterRequestDTO request = new RegisterRequestDTO(
                "newuser@example.com",
                "password123",
                "newuser",
                "NewUser",
                "GOOGLE",
                "ROLE_ADMIN"
        );
        LoginResponseDTO customResponse = new LoginResponseDTO(
                3,
                "newuser@example.com",
                "newuser",
                "NewUser",
                "GOOGLE",
                "ROLE_ADMIN",
                "2026-05-08T10:00:00",
                "jwt-token-789"
        );
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(customResponse);

        // When: POST /api/v1/auth/register with custom auth provider
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Then: Status is 200 OK and response has custom provider and role
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authProvider").value("GOOGLE"))
                .andExpect(jsonPath("$.roleUser").value("ROLE_ADMIN"));

        // Verify service was called once
        verify(authService, times(1)).register(any(RegisterRequestDTO.class));
    }

    
    @Test
    void testRegister_InternalServerError() throws Exception {
        // Given: Service throws unexpected exception
        RegisterRequestDTO request = new RegisterRequestDTO(
                "newuser@example.com",
                "password123",
                "newuser",
                "NewUser",
                null,
                null
        );
        when(authService.register(any(RegisterRequestDTO.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // When: POST /api/v1/auth/register and service fails
        // Then: Status is 500 Internal Server Error
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        // Verify service was called once
        verify(authService, times(1)).register(any(RegisterRequestDTO.class));
    }

    
    @Test
    void testRegister_EmptyBody() throws Exception {
        // Given: Empty request body
        // When: POST /api/v1/auth/register with empty body
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    }

    
    @Test
    void testRegister_MalformedJson() throws Exception {
        // Given: Malformed JSON
        // When: POST /api/v1/auth/register with invalid JSON
        // Then: Status is 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{malformed}"))
                .andExpect(status().isBadRequest());

        // Verify service was not called
        verifyNoInteractions(authService);
    } 

}
