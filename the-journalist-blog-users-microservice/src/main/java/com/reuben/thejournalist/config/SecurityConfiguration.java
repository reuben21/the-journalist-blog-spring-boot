package com.reuben.thejournalist.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;


import static commons.Role.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration // This annotation is used to mark a class as a configuration class
@EnableWebSecurity // This annotation is used to enable web security in the application
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {
            "/user/register",
            "/user/authenticate",
            "/swagger-ui.html"};
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    private final LogoutHandler logoutHandler = (request, response, authentication) -> {
        SecurityContextHolder.clearContext();
    };

    // This method is used to configure the security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/api/v1/demo-controller").hasAnyRole(ADMIN.name())
                                .requestMatchers("/api/v2/demo-controller").hasAnyRole(ADMIN.name(), AUTHOR.name())
                                .requestMatchers("/api/v3/demo-controller").hasAnyRole(ADMIN.name(), AUTHOR.name(), USER.name())
                                .requestMatchers("/user/**").hasAnyRole(ADMIN.name(), AUTHOR.name(), USER.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .addLogoutHandler(logoutHandler)
                        .logoutUrl("/user/logout")
                        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(200))
                );


        return http.build();

    }

}
