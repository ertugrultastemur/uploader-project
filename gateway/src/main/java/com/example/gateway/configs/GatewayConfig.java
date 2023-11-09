package com.example.gateway.configs;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.RouteMatcher;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Configuration
public class GatewayConfig {
    private final AuthenticationFilter authenticationFilter = new AuthenticationFilter(new RestTemplate());



    @Bean
    public RestTemplate template(){
        return new RestTemplate();
    }




    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, AuthenticationFilter authenticationFilter, AuthorizationFilter authorizationFilter, RouteValidator routeValidator) {

        AuthenticationFilter.Config config = new AuthenticationFilter.Config();

        GatewayFilter filter = authenticationFilter.apply(config);
        return builder.routes()
                .route("user-management-service", r -> r.path("/v1/users/**")
                        .filters(f ->
                            f.filter(filter)
                            .filter(authorizationFilter))
                        .uri("http://localhost:5353"))
                .route("uploader-service", r -> r.path("/v1/doc/**")
                        .filters(f ->
                            f.filter(filter)
                            .filter(authorizationFilter))
                        .uri("http://localhost:8383"))
                .route("authentication-service", r -> r.path("/v1/auth/**")
                        .filters(f ->
                            f.filter(routeValidator))
                        .uri("http://localhost:9001"))
                .build();
    }


}