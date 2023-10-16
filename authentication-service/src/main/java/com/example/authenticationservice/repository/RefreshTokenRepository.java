package com.example.authenticationservice.repository;

import java.util.Optional;

import com.example.authenticationservice.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByKey (String key);

}
