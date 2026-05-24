package com.smart.billing.app.config;


import com.smart.billing.app.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security configuration class for the Smart Billing application.
 *
 * This configuration class provides essential Spring Security beans for authentication
 * and user management. It configures the authentication provider, password encoder,
 * user details service, and authentication manager required for secure user authentication.
 *
 * Key components configured:
 * - UserDetailsService for loading user details by email
 * - BCryptPasswordEncoder for secure password hashing
 * - DaoAuthenticationProvider for database-based authentication
 * - AuthenticationManager for handling authentication requests
 *
 * @author Smart Billing Team
 * @version 1.0
 * @since 1.0
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AuthRepository authRepository;


    /**
     * Creates a UserDetailsService bean for loading user details by email.
     *
     * This service is used by Spring Security to retrieve user information
     * during the authentication process. It searches for users by email address
     * in the authentication repository and converts them to UserDetails objects.
     *
     * @return UserDetailsService implementation that loads users by email
     * @throws UsernameNotFoundException if no user is found with the given email
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> authRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }


    /**
     * Creates a PasswordEncoder bean using BCrypt hashing algorithm.
     *
     * This encoder is used throughout the application for securely hashing
     * user passwords during registration and for verifying passwords during
     * authentication. BCrypt provides strong password hashing with salt.
     *
     * @return BCryptPasswordEncoder for secure password operations
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Creates an AuthenticationProvider bean for database authentication.
     *
     * This provider uses the DAO (Data Access Object) pattern to authenticate
     * users against the database. It combines the UserDetailsService for user
     * lookup and PasswordEncoder for password verification.
     *
     * @return DaoAuthenticationProvider configured with user details service and password encoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService()); // Llamada al método Bean
        provider.setPasswordEncoder(passwordEncoder());      // Llamada al método Bean
        return provider;
    }


    /**
     * Creates an AuthenticationManager bean for handling authentication requests.
     *
     * The AuthenticationManager is the main entry point for authentication in Spring Security.
     * It delegates to configured authentication providers to perform the actual authentication.
     *
     * @param config the AuthenticationConfiguration provided by Spring Security
     * @return AuthenticationManager for processing authentication requests
     * @throws Exception if authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
