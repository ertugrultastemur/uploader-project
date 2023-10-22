package com.example.gateway.configs;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class GatewayConfig {
    private final AuthenticationFilter authenticationFilter = new AuthenticationFilter(new RestTemplate());


    @Bean
    public HasRoleRoutePredicateFactory hasRoleRoutePredicateFactory() {
        return new HasRoleRoutePredicateFactory();
    }

    @Bean
    public RestTemplate template(){
        return new RestTemplate();
    }


    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, AuthenticationFilter authenticationFilter, AuthorizationFilter authorizationFilter) {

        AuthenticationFilter.Config config = new AuthenticationFilter.Config();
        //config.setRoles(Arrays.asList("ROLE_ADMIN", "ROLE_USER"));

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
                .route("security", r -> r.path("/v1/auth/**")
                        .filters(f ->
                            f.filter(filter)
                            .filter(authorizationFilter))
                        .uri("http://localhost:9001"))
                .build();
    }
    @Bean
    public Map<String, List<String>> allowedRolesPathMapping() {
        Map<String, List<String>> mapping = new HashMap<>();

        // Define the mappings between paths and allowed roles
        mapping.put("/v1/doc", Arrays.asList("ROLE_USER"));
        mapping.put("/v1/auth", Arrays.asList("ROLE_ADMIN","ROLE_OPERATOR"));

        return mapping;
    }
}