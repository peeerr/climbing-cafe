package com.peeerr.climbing.security;

import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import com.peeerr.climbing.exception.EntityNotFoundException;
import java.util.Optional;
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
        // TODO:  logout 어케하는지 (구현)
        //        login, logout 주석해논거 다 처리
        //        전체적인 흐름 복습 공부 & 블로그 정리
        Member member = memberRepository.findMemberByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.LOGIN_FAILED));

        return new MemberPrincipal(member);
    }

}
