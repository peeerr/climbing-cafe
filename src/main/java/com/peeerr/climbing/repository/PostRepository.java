package com.peeerr.climbing.repository;

import com.peeerr.climbing.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

    @Query(value = "SELECT GET_LOCK(:key, 3000)", nativeQuery = true)
    void getNamedLock(String key);

    @Query(value = "SELECT RELEASE_LOCK(:key)", nativeQuery = true)
    void releaseNamedLock(String key);

}
