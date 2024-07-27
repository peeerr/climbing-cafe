package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.dto.request.MemberCreateRequest;
import com.peeerr.climbing.dto.request.MemberEditRequest;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.repository.MemberRepository;
import com.peeerr.climbing.service.validator.MemberValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberValidator memberValidator;

    @InjectMocks
    private MemberService memberService;

    @DisplayName("회원 한 명을 추가한다.")
    @Test
    void addMember() throws Exception {
        //given
        MemberCreateRequest request = MemberCreateRequest.of("test", "test1234", "test1234", "test@example.com");
        Member member = Member.builder().build();

        willDoNothing().given(memberValidator).validateNewMember(any(MemberCreateRequest.class));
        given(memberRepository.save(any(Member.class))).willReturn(member);

        //when
        memberService.addMember(request);

        //then
        then(memberValidator).should().validateNewMember(any(MemberCreateRequest.class));
        then(memberRepository).should().save(any(Member.class));
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
        willDoNothing().given(memberValidator).validateDuplicateEmail(anyString());
        willDoNothing().given(memberValidator).validateDuplicateUsername(anyString());

        //when
        memberService.editMember(memberId, request, loginId);

        //then
        then(memberRepository).should().findById(memberId);
        then(memberValidator).should().validateDuplicateEmail(anyString());
        then(memberValidator).should().validateDuplicateUsername(anyString());
    }

    @DisplayName("[접근 권한X] 회원 정보를 수정하는데, 로그인 사용자와 수정 대상자가 다르면 예외를 던진다.")
    @Test
    void editMemberWithoutPermission() throws Exception {
        //given
        Long memberId = 1L;
        Long loginId = 2L;

        MemberEditRequest request = MemberEditRequest.of("editTest", "editTest@example.com");

        Member member = Member.builder()
                .id(memberId)
                .username("test")
                .email("test@example.com")
                .build();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        //when & then
        assertThatExceptionOfType(ClimbingException.class)
                .isThrownBy(() -> memberService.editMember(memberId, request, loginId));

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
        assertThatExceptionOfType(ClimbingException.class)
                .isThrownBy(() -> memberService.editMember(memberId, request, loginId));

        then(memberRepository).should().findById(memberId);
    }

}
