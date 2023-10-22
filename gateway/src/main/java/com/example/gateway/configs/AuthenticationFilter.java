package com.example.gateway.configs;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {


    private final RestTemplate template;

    private final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    public String getRoless(String token) {
        // Validate token
        DecodedJWT decodedJWT = JWT.decode(token);

        // Get "role" claim if it exists
        Claim roleClaim = decodedJWT.getClaim("role");


        if (roleClaim.isNull() || roleClaim.asString() == null) {
            // "role" claim doesn't exist or is null
            return "No Role Found"; // veya istediğiniz bir varsayılan değeri döndürebilirsiniz
        }

        return roleClaim.asString();
    }

    public String getRoles(String token) {
        return template.getForEntity("http://localhost:9001/v1/auth/validate?token=" + token, String.class).getBody();

    }

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

                if (response.getStatusCode() == HttpStatus.OK ) {
                    // Token doğrulama başarılı
                    String roles = getRoles(authToken);
                    List<String> userRoles = Arrays.asList(roles.split(",")); // Kullanıcı rollerini virgülle ayrılmış bir liste olarak alın
                    // Kullanıcı rollerini kontrol et

                    /*List<String> allowedRoles = config.getRoles();
                    for (String userRole : userRoles) {
                        if (!allowedRoles.contains(userRole)) {
                            logger.error("AuthenticationFilter: Unauthorized access to application. User role is not allowed.");
                            throw new RuntimeException("Unauthorized access to application. User role is not allowed.");
                        }
                    }*/
                } else {
                    System.out.println("invalid access...!");
                    logger.error("AuthenticationFilter: Unauthorized access to application.");
                    throw new RuntimeException("Unauthorized access to application");
                }
            } catch (RestClientResponseException e) {
                // Uzak sunucudan hata yanıtı alındığında burada işleyebilirsiniz.
                // Örneğin, HTTP durum kodlarına göre özel işlemler yapabilirsiniz.
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
        private List<String> roles;


        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}