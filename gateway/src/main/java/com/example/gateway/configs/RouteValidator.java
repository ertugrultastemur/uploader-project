package com.example.gateway.configs;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Service
public class RouteValidator implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Burada, özel bir filtreleme mantığı ekleyebilirsiniz.
        // OpenEndpoints listesindeki yolları kontrol etmek için aşağıdaki gibi bir kontrol ekleyebilirsiniz.
        if (isSecured.test(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        // Filtreleme yapmak istemediğiniz yolları bu blokta işlem geçirin.
        // Aksi takdirde, işlemi sonlandırın veya hata yanıtı gönderin.
         return chain.filter(exchange);
    }

    public static final List<String> openEndpoints = List.of(
            "/v1/auth/register",
            "/v1/auth/login",
            "/v1/auth/validate",
            "/eureka"
    );



    public Predicate<ServerHttpRequest> isSecured =
            request -> openEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));


}