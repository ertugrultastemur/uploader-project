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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public MemberResponse signup(SignupRequest signupRequest) {
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Please use another email.");
        }

        Member member = signupRequest.toMember(passwordEncoder);
        return MemberResponse.of(memberRepository.save(member));
    }

    @Transactional
    public TokenRequest signin(SigninRequest signinRequest) {
        // 1. Generate Authentication Token based on signin ID and PW
        UsernamePasswordAuthenticationToken authenticationToken = signinRequest.toAuthentication();

        // 2. Password check
        //    when authenticate method is called, loadUserByUsername in CustomUserDetailsService is called
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. Generate JWT token based on given info
        TokenRequest tokenRequest = tokenProvider.generateTokenDto(authentication);

        // 4. Save RefreshToken
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenRequest.getRefreshToken())
                .build();
                
        refreshTokenRepository.save(refreshToken);
        
        // 5. Generate Token
        return tokenRequest;
    }

    @Transactional
    public TokenRequest reissue(TokenRequest tokenRequest) {
        // 1. validate a Refresh Token
        if (!tokenProvider.validateToken(tokenRequest.getRefreshToken())) {
            throw new RuntimeException("Invail Refresh Token.");
        }

        // 2. Fetch Member ID from an access token
        Authentication authentication = tokenProvider.getAuthentication(tokenRequest.getAccessToken());

        // 3. Fetch the Refresh token value bacsed on member ID
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("This member is signed out."));

        // 4. check if Refresh Token is matched
        if (!refreshToken.getValue().equals(tokenRequest.getRefreshToken())) {
            throw new RuntimeException("Token's member info is not matched.");
        }

        // 5. create a new token
        TokenRequest newTokenRequest = tokenProvider.generateTokenDto(authentication);

        // 6. update the refresh token
        RefreshToken newRefreshToken = refreshToken.updateValue(newTokenRequest.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // return the new refresh token
        return newTokenRequest;
    }
    
}
