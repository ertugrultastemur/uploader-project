package com.example.authenticationservice.dto;



import com.example.authenticationservice.domain.Member;
import jakarta.validation.constraints.Email;
import org.springframework.security.crypto.password.PasswordEncoder;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Data
public class SignupRequest {
    
    @Email(message = "Not a valid email")
    private String email;

    private String password;

    private Set<String> roles;

    public Member toMember(PasswordEncoder passwordEncoder) {
        Member member = new Member(email, passwordEncoder.encode(password),roles);
        return member;
    }
    
}
