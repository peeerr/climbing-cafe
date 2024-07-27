package com.peeerr.climbing.repository;

import com.peeerr.climbing.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {
}
