package com.peeerr.climbing.repository;

import com.peeerr.climbing.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
