package com.example.authenticationservice.controller;


import com.example.authenticationservice.dto.AuthResponse;
import com.example.authenticationservice.dto.SigninRequest;
import com.example.authenticationservice.dto.SignupRequest;
import com.example.authenticationservice.dto.TokenRequest;
import com.example.authenticationservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<TokenRequest> login(@RequestBody @Valid final SigninRequest signinRequest) {
        return ResponseEntity.ok(authService.signin(signinRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid final SignupRequest signupRequest) {
        authService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validate(@RequestParam("token") String token){
        return ResponseEntity.ok(authService.validateToken(token));
    }

    @PostMapping("/token")
    public ResponseEntity<AuthResponse> token() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenRequest> reissue(@RequestBody TokenRequest tokenRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
    }
}
