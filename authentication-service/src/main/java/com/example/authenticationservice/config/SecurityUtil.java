package com.example.authenticationservice.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecurityUtil {

    // At the moment that a member info is saved in SecurityContext
    // Save a request in JwtFilter
    public static String getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw  new RuntimeException("There's no authentication info in the security context");
        }

        return authentication.getName();
    }

}
