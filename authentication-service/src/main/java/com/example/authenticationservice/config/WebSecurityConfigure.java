package com.example.authenticationservice.config;


import com.example.authenticationservice.exceptions.JwtAccessDeniedHandler;
import com.example.authenticationservice.exceptions.JwtAuthenticationEntryPoint;
import com.example.authenticationservice.infrastructure.TokenProvider;
import lombok.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;


import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfigure {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF config Disable
    http.csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(eh-> eh.authenticationEntryPoint(jwtAuthenticationEntryPoint).accessDeniedHandler(jwtAccessDeniedHandler))
        .headers(header-> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .sessionManagement(sManagement-> sManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(requests -> requests
                .requestMatchers("/", "/home")
                .authenticated().anyRequest().permitAll())
        .apply(new JwtSecurityConfig(tokenProvider));
        return http.build();
    }



}
