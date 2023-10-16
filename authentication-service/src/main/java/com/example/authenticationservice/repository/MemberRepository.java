package com.example.authenticationservice.repository;

import java.util.Optional;

import com.example.authenticationservice.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail (String email);
    boolean existsByEmail (String email);
}
