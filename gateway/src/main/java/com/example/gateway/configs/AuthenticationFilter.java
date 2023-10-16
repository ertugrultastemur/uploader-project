package com.example.gateway.configs;

import com.example.gateway.service.JwtUtils;
import com.example.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RefreshScope
@Component
//@ConfigurationProperties("authentication-filter")
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
/*

    private final RouteValidator validator;

    private final JwtUtils jwtUtils;

    public AuthenticationFilter(RouteValidator validator, JwtUtils jwtUtils) {
        this.validator = validator;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (validator.isSecured.test(request)) {
            if (authMissing(request)) {
                return onError(exchange);
            }

            final String token = request.getHeaders().getOrEmpty("Authorization").get(0);

            if (jwtUtils.isExpired(token)) {
                return onError(exchange);
            }
        }
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    private boolean authMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }
    */

    Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final RouteValidator validator;

    private final RestTemplate template;
    private final JwtUtil jwtUtil;


    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil, RestTemplate template) {
        super(AuthenticationFilter.Config.class);
        logger.info("AuthenticationFilter: Entered constructor.");
        this.validator = validator;
        this.jwtUtil = jwtUtil;
        this.template = template;
    }

    /**
     * Applies the authentication filter to the incoming request.
     *
     * @param config the configuration object for the filter
     * @return the GatewayFilter instance
     */
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
                template.getForObject("http://localhost:9090/api/v1/auth/validate?token=" + authToken, String.class);
            } catch (Exception e) {
                System.out.println("invalid access...!"+e.getMessage());
                logger.error("AuthenticationFilter: Unauthorized access to application."+ e.getMessage());
                throw new RuntimeException("Unauthorized access to application");
            }
            logger.info("AuthenticationFilter: Token valid.");
            return chain.filter(exchange);
        };
    }

    /**
     * Configuration class for the authentication filter.
     */
    public static class Config {
        private List<String> roles;

        /**
         * Retrieves the list of roles.
         *
         * @return the list of roles
         */
        public List<String> getRoles() {
            return roles;
        }

        /**
         * Sets the list of roles.
         *
         * @param roles the list of roles
         */
        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}