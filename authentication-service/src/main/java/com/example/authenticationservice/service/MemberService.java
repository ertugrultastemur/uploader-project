package com.example.authenticationservice.service;


import com.example.authenticationservice.config.SecurityUtil;
import com.example.authenticationservice.dto.MemberResponse;
import com.example.authenticationservice.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    public MemberResponse getMemberInfo(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberResponse::of)
                .orElseThrow(() -> new RuntimeException("There's no that member info."));
    }

    public MemberResponse getCurrMemberInfo(String email) {
        return memberRepository.findByEmail(SecurityUtil.getCurrentMemberId())
                .map(MemberResponse::of)
                .orElseThrow(() -> new RuntimeException("There's no login member."));
    }
}
