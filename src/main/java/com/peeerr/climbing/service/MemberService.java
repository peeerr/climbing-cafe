package com.peeerr.climbing.service;

import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.dto.member.MemberCreateRequest;
import com.peeerr.climbing.dto.member.MemberEditRequest;
import com.peeerr.climbing.entity.Member;
import com.peeerr.climbing.exception.AccessDeniedException;
import com.peeerr.climbing.exception.ValidationException;
import com.peeerr.climbing.exception.already.AlreadyExistsCategoryException;
import com.peeerr.climbing.exception.already.AlreadyExistsEmailException;
import com.peeerr.climbing.exception.already.AlreadyExistsUsernameException;
import com.peeerr.climbing.exception.notfound.MemberNotFoundException;
import com.peeerr.climbing.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
                .orElseThrow(() -> new MemberNotFoundException());

        if (!member.getId().equals(loginId)) {
            throw new AccessDeniedException();
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
            throw new AlreadyExistsCategoryException();
        });
        existingEmail.ifPresent(foundMember -> {
            throw new AlreadyExistsCategoryException();
        });
    }

    public void validateDuplicateUsername(String username) {
        Optional<Member> existingUsername = memberRepository.findMemberByUsername(username);

        existingUsername.ifPresent(foundMember -> {
            throw new AlreadyExistsUsernameException();
        });
    }

    public void validateDuplicateEmail(String email) {
        Optional<Member> existingEmail = memberRepository.findMemberByEmail(email);

        existingEmail.ifPresent(foundMember -> {
            throw new AlreadyExistsEmailException();
        });
    }

}
