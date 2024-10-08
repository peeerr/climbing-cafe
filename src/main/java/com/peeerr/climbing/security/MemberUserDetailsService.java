package com.peeerr.climbing.security;

import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MemberUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.LOGIN_FAILED.getMessage()));

        return new MemberPrincipal(member);
    }

}
