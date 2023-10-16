package com.example.authenticationservice.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.example.authenticationservice.models.ApplicationUser;
import com.example.authenticationservice.models.LoginResponseDTO;
import com.example.authenticationservice.models.RegistrationDTO;
import com.example.authenticationservice.models.Role;
import com.example.authenticationservice.repository.RoleRepository;
import com.example.authenticationservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public ApplicationUser registerUser(RegistrationDTO body){

        String encodedPassword = passwordEncoder.encode(body.getPassword());
        Role userRole = roleRepository.findByAuthority("ADMIN").get();

        Set<Role> authorities = new HashSet<>();

        authorities.add(userRole);

        return userRepository.save(new ApplicationUser(0, body.getUsername(), encodedPassword, authorities));
    }

    public LoginResponseDTO loginUser(RegistrationDTO body){

        try{
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword())
            );

            String token = tokenService.generateJwt(auth);

            return new LoginResponseDTO(userRepository.findByUsername(body.getUsername()).get(), token);

        } catch(AuthenticationException e){
            return new LoginResponseDTO(null, "");
        }
    }

    public String validate(String token){
        if (tokenService.validateToken(token)) {
            return "Token is valid.";
        }
        else{
            return "Token is not valid.";
        }
    }

}
