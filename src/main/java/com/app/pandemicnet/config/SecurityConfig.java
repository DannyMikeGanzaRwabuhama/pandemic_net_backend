package com.app.pandemicnet.config;

import com.app.pandemicnet.util.JwtAuthFilter;
import com.app.pandemicnet.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Added from Andrew
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Added from main
import org.springframework.security.crypto.password.PasswordEncoder; // Added from main
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Removed unused import: import java.util.Properties;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // Added from main
public class SecurityConfig {

    private final JwtService jwtService; // Added from main

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Permitted paths from main branch (Swagger, user registration/login)
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/v3/api-docs.yaml/**"
                        ).permitAll()
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()

                        // Permitted paths from Andrew's branch
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/entries", // Assuming this is for public access to entries
                                "/error"
                        ).permitAll()

                        // Authenticated paths
                        .requestMatchers("/api/users/**").authenticated() // Secure user-related endpoints
                        .anyRequest().authenticated() // All other requests must be authenticated
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class) // Added from main
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { // Added from main
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() { // Added from main
        return new JwtAuthFilter(jwtService);
    }
}