package com.reuben.thejournalist.config;

import com.reuben.thejournalist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {


    private final UserService userService;
    @Bean
    // This is the userDetailsService that will be used to load user details from the database
    public UserDetailsService userDetailsService() {
       return username -> userService.loadUserByUsername(username.toLowerCase());
    }

    @Bean
    // This is the authentication provider that will be used to authenticate users
    public AuthenticationProvider authenticationProvider() {
        // This is the authentication provider that will be used to authenticate users
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // This is the userDetailsService that will be used to load user details
        provider.setUserDetailsService(userDetailsService());
        // This is the password encoder that will be used to encode and verify passwords
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    // This is the authentication manager that will be used to authenticate users
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // This is the password encoder that will be used to encode and verify passwords
        return new BCryptPasswordEncoder();
    }
}
