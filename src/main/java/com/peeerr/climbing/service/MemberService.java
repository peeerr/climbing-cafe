package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.dto.request.MemberCreateRequest;
import com.peeerr.climbing.dto.request.MemberEditRequest;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.MemberRepository;
import com.peeerr.climbing.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberValidator memberValidator;

    public void addMember(MemberCreateRequest request) {
        memberValidator.validateNewMember(request);

        Member member = Member.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        memberRepository.save(member);
    }

    public void editMember(Long memberId, MemberEditRequest request, Long loginId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.MEMBER_NOT_FOUND));
        member.checkOwner(loginId);

        if (!member.getEmail().equals(request.getEmail())) {
            memberValidator.validateDuplicateEmail(request.getEmail());
            member.changeEmail(request.getEmail());
        }

        if (!member.getUsername().equals(request.getUsername())) {
            memberValidator.validateDuplicateUsername(request.getUsername());
            member.changeUsername(request.getUsername());
        }
    }

}
