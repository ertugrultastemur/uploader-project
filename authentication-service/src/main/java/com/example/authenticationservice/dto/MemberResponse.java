package com.example.authenticationservice.dto;

import com.example.authenticationservice.domain.Member;
import lombok.Getter;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberResponse {

    private Long id;
    private String email;

    public static MemberResponse of(final Member member) {
        return new MemberResponse(member.getId(), member.getEmail());
    }



}
