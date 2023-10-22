package com.example.gateway.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Component
public class HasRoleRoutePredicateFactory
        extends AbstractRoutePredicateFactory<HasRoleRoutePredicateFactory.Config> {

    private final AuthenticationFilter authenticationFilter = new AuthenticationFilter(new RestTemplate());

    public HasRoleRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("role");
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        List<String> hasRoles = Arrays.stream(config.getRole().split(",")).toList();
        try {
            return exchange -> {
                String requestPath = exchange.getRequest().getURI().getPath();

                // Gelen requestPath ile Route Configuration'daki path'e göre eşleşme kontrolü
                if (matchesPath(requestPath, config.getPath())) {
                    if (checkHasRole(exchange, config.getRole()))
                    {
                        throw new RuntimeException("as");
                    }else {
                        throw new RuntimeException("sasassasa");
                    }
                }

                return false;
            };
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }
    private boolean matchesPath(String requestPath, String routePath) {
        // URL path ve Route path arasındaki eşleşmeyi kontrol et
        return requestPath.contains(routePath);
    }

    private boolean checkHasRole(ServerWebExchange exchange, String hasRole) {
        // "HasRole" değerini kontrol etmek için gereken mantığı ekleyin
        // Örneğin, kullanıcının rolleri HTTP başlıklarından alınabilir.
        // Eğer belirtilen rol varsa true döndürün, aksi halde false döndürün.
        String rolesHeader = authenticationFilter.getRoles(exchange.getRequest().getHeaders().getFirst("Authorization"));
        if (rolesHeader != null) {
            List<String> userRoles = new ArrayList<>(Arrays.asList(rolesHeader.split(",")));
            return userRoles.contains(hasRole);
        }
        return false;
    }
    @Getter
    public static class Config {
        private String path;

        private String role;

        public void setPath(String path) {
            this.path = path;
        }
        public void setRole(String role) {
            this.role = role;
        }
    }


}