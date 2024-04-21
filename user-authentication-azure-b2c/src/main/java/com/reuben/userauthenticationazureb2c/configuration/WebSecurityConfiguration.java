package com.reuben.userauthenticationazureb2c.configuration;


import com.azure.spring.cloud.autoconfigure.implementation.aadb2c.security.AadB2cOidcLoginConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // This annotation is used to mark a class as a configuration class
@EnableWebSecurity // This annotation is used to enable web security in the application
public class WebSecurityConfiguration {

    private final AadB2cOidcLoginConfigurer configurer;

    public WebSecurityConfiguration(AadB2cOidcLoginConfigurer configurer) {
        this.configurer = configurer;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        configurer.init(http);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .anyRequest().authenticated()
                );
//                .apply(configurer);
        return http.build();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .apply(configurer)
//        ;
//    }
}