package com.example.gateway.configs;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {


    private final RestTemplate template;

    private final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    

    @Override
    public GatewayFilter apply(Config config) {
        logger.info("AuthenticationFilter: Entered apply method.");
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (token == null || !token.startsWith("Bearer ")) {
                logger.error("AuthenticationFilter: Missing or invalid Authorization header.");
                throw new RuntimeException("Missing or invalid Authorization header");
            }


            String authToken = token.substring(7);

            try {
                ResponseEntity<String> response = template.getForEntity("http://localhost:9001/v1/auth/validate?token=" + authToken, String.class);

                if (!(response.getStatusCode() == HttpStatus.OK )) {
                    System.out.println("invalid access...!");
                    logger.error("AuthenticationFilter: Unauthorized access to application.");
                    throw new RuntimeException("Unauthorized access to application");
                }
            } catch (RestClientResponseException e) {
                System.out.println("HTTP Error: " + e.getStatusCode());
                logger.error("AuthenticationFilter: Unauthorized access to application. HTTP Error: " + e.getStatusCode());
                throw new RuntimeException("Unauthorized access to application");
            } catch (Exception e) {
                System.out.println("Connection error...!");
                logger.error("AuthenticationFilter: Connection error: " + e.getMessage());
                throw new RuntimeException("Unauthorized access to application");
            }

            logger.info("AuthenticationFilter: Token valid.");
            return chain.filter(exchange);
        };
    }

    @Getter
    public static class Config {

    }
}