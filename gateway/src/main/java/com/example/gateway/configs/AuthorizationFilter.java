package com.example.gateway.configs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationFilter implements GatewayFilter {
    private final RestTemplate template;

    public AuthorizationFilter(RestTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());

        String role = validateToken(token).getBody();

        String requestPath = request.getPath().toString();
        if (requestPath.startsWith("/v1/doc")) {
            if (!role.contains("ROLE_ADMIN")) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                log.info("Unauthorized access: "+ role + HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        } else if (requestPath.startsWith("/v1/auth")) {
            if (!role.contains("ROLE_USER")) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                log.info("Unauthorized access: "+ role + HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private ResponseEntity<String> validateToken(String token) {
        return template.getForEntity("http://localhost:9001/v1/auth/validate?token=" + token, String.class);
    }

}


