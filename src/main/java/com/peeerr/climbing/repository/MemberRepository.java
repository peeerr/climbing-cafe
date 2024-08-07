package com.peeerr.climbing.repository;

import com.peeerr.climbing.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByEmail(String email);

    Optional<Member> findMemberByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
