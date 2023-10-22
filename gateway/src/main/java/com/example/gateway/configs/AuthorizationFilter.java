package com.example.gateway.configs;

import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Map;

@Component
public class AuthorizationFilter implements GatewayFilter {
    private final RestTemplate template;


    private final Map<String, List<String>> allowedRolesPathMapping; // Yollar ve izin verilen rollerin haritası

    public AuthorizationFilter(Map<String, List<String>> allowedRolesPathMapping, RestTemplate template) {
        this.allowedRolesPathMapping = allowedRolesPathMapping;
        this.template = template;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Authorization header'dan tokeni çıkarın
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = authorizationHeader.substring("Bearer ".length());

        String role = validateToken(token).getBody();

        // İstenen yolu alın
        String requestPath = request.getPath().toString();
        String path= pathSplit(requestPath,3);
        // İstenilen yol için rolün izin verilip verilmediğini kontrol edin
        List<String> allowedRoles = allowedRolesPathMapping.get(path);
        // allowedRoles bir List<String> türünde ise
        List<String> allowedRolesList = allowedRolesPathMapping.get(path);

        // role bir tek String olarak "ROLE_USER,ROLE_ADMIN" gibi bir değeri temsil ediyorsa
        String[] userRoles = role.split(",");

        boolean isAuthorized = false;

        for (String userRole : userRoles) {
            if (allowedRolesList.contains(userRole)) {
                isAuthorized = true;
                break;
            }
        }

        if (!isAuthorized) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        // Rol izinlidir, istekle devam edin
        return chain.filter(exchange);
    }

    private ResponseEntity<String> validateToken(String token) {
        return template.getForEntity("http://localhost:9001/v1/auth/validate?token=" + token, String.class);
    }

    private String pathSplit(String path, int splitNumber){
// URL'den sadece "/v1/doc" kısmını almak
        String[] parts = path.split("/"); // URL'yi "/" karakterine göre böler

        if (parts.length >= splitNumber) {
            String desiredPart = "/" + parts[1] + "/" + parts[2];
            return desiredPart;
        } else {
            throw new RuntimeException();
        }
    }
}


