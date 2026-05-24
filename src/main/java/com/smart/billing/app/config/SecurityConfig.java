    package com.smart.billing.app.config;

    import java.util.List;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.CorsConfigurationSource;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

    import lombok.RequiredArgsConstructor;

    @Configuration
    @EnableWebSecurity
    @RequiredArgsConstructor
    public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain filterChainXX(HttpSecurity httpSecurity) throws Exception {
            httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Enable CORS
                // Disable CSRF protection for API requests
                .csrf(csrf -> csrf.disable())
                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                    // Allow public access to Swagger/API documentation
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                    ).permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                    // Allow public access to API endpoints
                    .requestMatchers(
                        "/api/**",
                        "/api/v1/auth/**"
                    ).permitAll()
                    // All other requests require authentication
                    .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                // We inject the JWT filter before the username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

            return httpSecurity.build();
        }

        

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

            CorsConfiguration config = new CorsConfiguration();

            config.setAllowedOriginPatterns(List.of(
                "http://localhost:4200",
                "https://*.app.github.dev"
            )); 

            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
            config.setExposedHeaders(List.of("Authorization"));
            config.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);

            return source;
        }


    }
