package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import com.peeerr.climbing.dto.member.MemberCreateRequest;
import com.peeerr.climbing.dto.member.MemberEditRequest;
import com.peeerr.climbing.dto.member.MemberLoginRequest;
import com.peeerr.climbing.exception.DuplicationException;
import com.peeerr.climbing.exception.EntityNotFoundException;
import com.peeerr.climbing.exception.UnauthorizedAccessException;
import com.peeerr.climbing.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @DisplayName("회원 로그인 한다.")
    @Test
    void login() throws Exception {
        //given
        String email = "test@example.com";
        String password = "test1234";

        MemberLoginRequest request = MemberLoginRequest.of(email, password);
        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();

        MockHttpSession session = new MockHttpSession();

        given(memberRepository.findMemberByEmail(email)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        //when
        memberService.login(request, session);

        //then
        then(memberRepository).should().findMemberByEmail(email);
        then(passwordEncoder).should().matches(anyString(), anyString());
    }

    @DisplayName("회원 로그인을 하는데, 존재하지 않는 아이디면 예외를 던진다.")
    @Test
    void loginWithNonExistingMember() throws Exception {
        //given
        String email = "test@example.com";
        String password = "test1234";

        MemberLoginRequest request = MemberLoginRequest.of(email, password);
        MockHttpSession session = new MockHttpSession();

        given(memberRepository.findMemberByEmail(email)).willReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> memberService.login(request, session));

        then(memberRepository).should().findMemberByEmail(email);
    }

    @DisplayName("회원 로그인을 하는데, 비밀번호가 일치하지 않으면 예외를 던진다.")
    @Test
    void loginWithInvalidPassword() throws Exception {
        //given
        String email = "test@example.com";

        MemberLoginRequest request = MemberLoginRequest.of(email, "test1234");
        Member member = Member.builder()
                .email(email)
                .password("good123")
                .build();

        MockHttpSession session = new MockHttpSession();

        given(memberRepository.findMemberByEmail(email)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        //when & then
        assertThrows(ValidationException.class,
                () -> memberService.login(request, session));

        //then
        then(memberRepository).should().findMemberByEmail(email);
        then(passwordEncoder).should().matches(anyString(), anyString());
    }

    @DisplayName("회원 로그아웃 한다.")
    @Test
    void logout() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", "test");

        //when
        memberService.logout(session);

        //then
        assertThat(session.getAttribute("MEMBER")).isNull();
    }

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

    @DisplayName("수정 정보를 받아 회원 정보를 수정한다.")
    @Test
    void editMember() throws Exception {
        //given
        Long memberId = 1L;
        Long loginId = 1L;

        String editUsername = "editTest";
        String editEmail = "editTest@example.com";
        MemberEditRequest request = MemberEditRequest.of(editUsername, editEmail);

        Member member = Member.builder()
                .id(loginId)
                .username("test")
                .email("test@example.com")
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        //when
        memberService.editMember(memberId, request, loginId);

        //then
        then(memberRepository).should().findById(memberId);
    }

    @DisplayName("[접근 권한X] 회원 정보를 수정하는데, 로그인 사용자와 수정 대상자가 다르면 예외를 던진다.")
    @Test
    void editMemberWithoutPermission() throws Exception {
        //given
        Long memberId = 1L;
        Long loginId = 1L;

        MemberEditRequest request = MemberEditRequest.of("editTest", "editTest@example.com");

        Member member = Member.builder()
                .id(2L)
                .username("test")
                .email("test@example.com")
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        //when & then
        assertThrows(UnauthorizedAccessException.class, () -> memberService.editMember(memberId, request, loginId));

        then(memberRepository).should().findById(memberId);
    }

    @DisplayName("수정 대상이 존재하지 않으면 예외를 던진다.")
    @Test
    void editMemberWithNonExistMember() throws Exception {
        //given
        Long memberId = 1L;
        Long loginId = 1L;

        MemberEditRequest request = MemberEditRequest.of("test", "test@example.com");
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> memberService.editMember(memberId, request, loginId));

        then(memberRepository).should().findById(memberId);
    }

    @DisplayName("회원 정보를 수정하는데, 중복된 닉네임이면 예외를 던진다.")
    @Test
    void editMemberWithDuplicatedUsername() throws Exception {
        //given
        Long memberId = 1L;
        Long loginId = 1L;

        String editUsername = "editTest";
        String editEmail = "test@example.com";
        MemberEditRequest request = MemberEditRequest.of(editUsername, editEmail);

        Member member = Member.builder()
                .id(loginId)
                .username("test")
                .email("test@example.com")
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.findMemberByUsername(editUsername)).willReturn(Optional.of(member));

        //when & then
        assertThrows(DuplicationException.class, () -> memberService.editMember(memberId, request, loginId));

        then(memberRepository).should().findById(memberId);
        then(memberRepository).should().findMemberByUsername(editUsername);
    }

    @DisplayName("회원 정보를 수정하는데, 중복된 이메일이면 예외를 던진다.")
    @Test
    void editMemberWithDuplicatedEmail() throws Exception {
        //given
        Long memberId = 1L;
        Long loginId = 1L;

        String editUsername = "test";
        String editEmail = "editTest@example.com";
        MemberEditRequest request = MemberEditRequest.of(editUsername, editEmail);

        Member member = Member.builder()
                .id(loginId)
                .username("test")
                .email("test@example.com")
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.findMemberByEmail(editEmail)).willReturn(Optional.of(member));

        //when & then
        assertThrows(DuplicationException.class, () -> memberService.editMember(memberId, request, loginId));

        then(memberRepository).should().findById(memberId);
        then(memberRepository).should().findMemberByEmail(editEmail);
    }

}
