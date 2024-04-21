package com.reuben.userauthenticationazureb2c;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


@SpringBootApplication
@EnableMethodSecurity
public class UserAuthenticationAzureB2cApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserAuthenticationAzureB2cApplication.class, args);
    }

}
