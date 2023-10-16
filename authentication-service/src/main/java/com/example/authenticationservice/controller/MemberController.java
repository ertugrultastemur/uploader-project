package com.example.authenticationservice.controller;

import com.example.authenticationservice.dto.MemberResponse;
import com.example.authenticationservice.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;



import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/member")
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;
    
    @GetMapping("/getMember/{email}")
    public ResponseEntity<MemberResponse> getMemberInfo (@PathVariable String email) {
        return ResponseEntity.ok(memberService.getMemberInfo(email));
    }

    @GetMapping("/curr-member/{email}")
    public ResponseEntity<MemberResponse> getCurrMemberInfo (@PathVariable String email) {
        return ResponseEntity.ok(memberService.getCurrMemberInfo(email));
    }

}
