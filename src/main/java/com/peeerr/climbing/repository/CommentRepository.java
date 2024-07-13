package com.peeerr.climbing.repository;

import com.peeerr.climbing.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
