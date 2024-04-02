package com.reuben.thejournalistapigateway;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
@Configuration
@EnableDiscoveryClient
public class GatewayRoutes {

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/user/**")
                        .uri("http://localhost:8081")

                ).route(p -> p
                        .path("/api/v1/**")
                        .uri("http://localhost:8081")
                ).build();
    }
}
