package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import com.peeerr.climbing.dto.member.request.MemberCreateRequest;
import com.peeerr.climbing.exception.ex.DuplicationException;
import com.peeerr.climbing.exception.ex.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @DisplayName("회원 한 명을 추가한다.")
    @Test
    void addMember() throws Exception {
        //given
        MemberCreateRequest request = MemberCreateRequest.of("test", "test1234", "test1234", "test@example.com");
        Member member = Member.builder().build();

        given(memberRepository.save(any(Member.class))).willReturn(member);

        //when
        memberService.addMember(request);

        //then
        then(memberRepository).should().save(any(Member.class));
    }

    @DisplayName("회원 한 명을 추가하는데, 비밀번호와 비밀번호 확인이 일치하지 않으면 예외를 던진다.")
    @Test
    void addMemberWithMismatchedPasswordConfirmation() throws Exception {
        //given
        MemberCreateRequest request = MemberCreateRequest.of("test", "test1234", "test12345", "test@example.com");

        //when & then
        assertThrows(ValidationException.class, () -> memberService.addMember(request));
    }

    @DisplayName("회원 한 명을 추가하는데, 이미 존재하는 이메일이거나 유저네임이면 예외를 던진다.")
    @Test
    void addMemberWithDuplicatedEmailOrUsername() throws Exception {
        //given
        MemberCreateRequest request = MemberCreateRequest.of("test", "test1234", "test1234", "test@example.com");
        Member member = Member.builder().build();

        given(memberRepository.findMemberByUsername(request.getUsername())).willReturn(Optional.of(member));
        given(memberRepository.findMemberByEmail(request.getEmail())).willReturn(Optional.of(member));

        //when & then
        assertThrows(DuplicationException.class, () -> memberService.addMember(request));

        then(memberRepository).should().findMemberByUsername(request.getUsername());
        then(memberRepository).should().findMemberByEmail(request.getEmail());
    }

}
