package com.example.authenticationservice.service;



import com.example.authenticationservice.domain.Member;
import com.example.authenticationservice.domain.RefreshToken;
import com.example.authenticationservice.dto.MemberResponse;
import com.example.authenticationservice.dto.SigninRequest;
import com.example.authenticationservice.dto.SignupRequest;
import com.example.authenticationservice.dto.TokenRequest;
import com.example.authenticationservice.infrastructure.TokenProvider;
import com.example.authenticationservice.repository.MemberRepository;
import com.example.authenticationservice.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void signup(SignupRequest signupRequest) {
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Please use another email.");
        }
        Member member = signupRequest.toMember(passwordEncoder);
        MemberResponse.of(memberRepository.save(member));
    }

    @Transactional
    public TokenRequest signin(SigninRequest signinRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = signinRequest.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        Member member = memberRepository.findByEmail(signinRequest.getEmail()).orElseThrow();
        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());

        for (String role : member.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        Authentication authenticatedUser = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities);

        TokenRequest tokenRequest = tokenProvider.generateTokenDto(authenticatedUser);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenRequest.getRefreshToken())
                .build();
                
        refreshTokenRepository.save(refreshToken);
        
        return tokenRequest;
    }

    @Transactional
    public String validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    @Transactional
    public TokenRequest reissue(TokenRequest tokenRequest) {
        // 1. validate a Refresh Token
        if (tokenProvider.validateToken(tokenRequest.getRefreshToken())==null) {
            throw new RuntimeException("Invalid Refresh Token.");
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenRequest.getAccessToken());

        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("This member is signed out."));

        if (!refreshToken.getValue().equals(tokenRequest.getRefreshToken())) {
            throw new RuntimeException("Token's member info is not matched.");
        }

        TokenRequest newTokenRequest = tokenProvider.generateTokenDto(authentication);

        RefreshToken newRefreshToken = refreshToken.updateValue(newTokenRequest.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return newTokenRequest;
    }
    
}
