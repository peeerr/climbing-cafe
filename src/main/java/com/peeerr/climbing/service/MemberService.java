package com.peeerr.climbing.service;

import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.entity.Member;
import com.peeerr.climbing.repository.MemberRepository;
import com.peeerr.climbing.dto.member.MemberCreateRequest;
import com.peeerr.climbing.dto.member.MemberEditRequest;
import com.peeerr.climbing.exception.DuplicationException;
import com.peeerr.climbing.exception.EntityNotFoundException;
import com.peeerr.climbing.exception.UnauthorizedAccessException;
import com.peeerr.climbing.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

//    @Transactional(readOnly = true)
//    public void login(MemberLoginRequest request, HttpSession session) {
//        Member member = memberRepository.findMemberByEmail(request.getEmail())
//                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MEMBER_NOT_FOUND));
//
//        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
//            throw new ValidationException(ErrorMessage.INVALID_PASSWORD);
//        }
//
//        MemberPrincipal userDetails = new MemberPrincipal(member);
//
//        session.setAttribute("MEMBER", userDetails);
//        session.setMaxInactiveInterval(1800);
//    }

//    public void logout(HttpSession session) {
//        session.removeAttribute("MEMBER");
//    }

    @Transactional
    public void addMember(MemberCreateRequest request) {
        validateDuplicateUser(request.getUsername(), request.getEmail());

        if (!request.getPassword().equals(request.getCheckPassword())) {
            throw new ValidationException(ErrorMessage.PASSWORD_CONFIRMATION_FAILED);
        }

        Member member = Member.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public void editMember(Long memberId, MemberEditRequest request, Long loginId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MEMBER_NOT_FOUND));

        if (!member.getId().equals(loginId)) {
            throw new UnauthorizedAccessException(ErrorMessage.NO_ACCESS_PERMISSION);
        }

        if (!member.getUsername().equals(request.getUsername())) {
            validateDuplicateUsername(request.getUsername());
            member.changeUsername(request.getUsername());
        }

        if (!member.getEmail().equals(request.getEmail())) {
            validateDuplicateEmail(request.getEmail());
            member.changeEmail(request.getEmail());
        }
    }

    public void validateDuplicateUser(String username, String email) {
        Optional<Member> existingUsername = memberRepository.findMemberByUsername(username);
        Optional<Member> existingEmail = memberRepository.findMemberByEmail(email);

        existingUsername.ifPresent(foundMember -> {
            throw new DuplicationException(ErrorMessage.USERNAME_DUPLICATED);
        });
        existingEmail.ifPresent(foundMember -> {
            throw new DuplicationException(ErrorMessage.EMAIL_DUPLICATED);
        });
    }

    public void validateDuplicateUsername(String username) {
        Optional<Member> existingUsername = memberRepository.findMemberByUsername(username);

        existingUsername.ifPresent(foundMember -> {
            throw new DuplicationException(ErrorMessage.USERNAME_DUPLICATED);
        });
    }

    public void validateDuplicateEmail(String email) {
        Optional<Member> existingEmail = memberRepository.findMemberByEmail(email);

        existingEmail.ifPresent(foundMember -> {
            throw new DuplicationException(ErrorMessage.EMAIL_DUPLICATED);
        });
    }

}
