package com.example.realestate.config;

import com.example.realestate.security.JwtAuthenticationEntryPoint;
import com.example.realestate.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main security configuration class for the application.
 * Defines authentication, authorization, and filter chain rules.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Allows the use of @PreAuthorize on controller methods
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    /**
     * Configures the security filter chain.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF as we are using JWT (stateless)
            .csrf().disable()
            
            // Handle unauthorized access attempts
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            
            // Ensure the server does not create a session (REST API requirement)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            
            // Define access rules for different endpoints
            .authorizeHttpRequests(auth -> auth
                // Publicly accessible endpoints (Auth, Listings, Static files)
                .requestMatchers(
                    "/api/auth/**", 
                    "/api/properties", 
                    "/api/properties/**", 
                    "/api/locations/**", 
                    "/login.html", 
                    "/register.html", 
                    "/dashboard.html", 
                    "/detail.html", 
                    "/style.css", 
                    "/detail.css", 
                    "/auth.js", 
                    "/dashboard.js", 
                    "/detail.js"
                ).permitAll()
                
                // All other requests must be authenticated
                .anyRequest().authenticated()
            );

        // Add the custom JWT filter before the standard UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * Password encoder bean using BCrypt hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean for use in the AuthenticationService.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}