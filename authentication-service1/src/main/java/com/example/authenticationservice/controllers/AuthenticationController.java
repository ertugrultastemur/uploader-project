package com.example.authenticationservice.controllers;

import com.example.authenticationservice.models.ApplicationUser;
import com.example.authenticationservice.models.LoginResponseDTO;
import com.example.authenticationservice.models.RegistrationDTO;
import com.example.authenticationservice.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/auth")
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApplicationUser> register(@RequestBody RegistrationDTO body){
        return ResponseEntity.ok(authenticationService.registerUser(body));
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody RegistrationDTO body){
        return ResponseEntity.ok(authenticationService.loginUser(body));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validate(@RequestParam("token") String token){
        return ResponseEntity.ok(authenticationService.validate(token));
    }
}
