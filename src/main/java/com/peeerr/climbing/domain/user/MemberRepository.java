package com.peeerr.climbing.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findUserByEmail(String username);

    Optional<Member> findUserByUsernameOrEmail(String username, String email);

}
