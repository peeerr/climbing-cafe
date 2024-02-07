package com.peeerr.climbing.dto.member.response;

import com.peeerr.climbing.domain.user.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberResponse {

    private String username;
    private String email;

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getUsername(), member.getEmail());
    }

}
