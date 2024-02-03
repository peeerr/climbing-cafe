package com.peeerr.climbing.config.auth;

import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findUserByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND));

        return new CustomUserDetails(member);
    }

}
