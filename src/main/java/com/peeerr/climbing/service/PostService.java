package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.dto.post.request.PostEditRequest;
import com.peeerr.climbing.dto.post.response.PostResponse;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        PostResponse postResponse = postRepository.findById(id)
                .map(PostResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시물을 찾을 수 없습니다."));

        return postResponse;
    }

    @Transactional
    public void addPost(Post post) {
        postRepository.save(post);
    }

    @Transactional
    public void editPost(Long id, PostEditRequest postEditRequest) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시물을 찾을 수 없습니다."));

        post.changeTitle(postEditRequest.getTitle());
        post.changeContent(postEditRequest.getContent());
    }

    @Transactional
    public void removePost(Long postId) {
        postRepository.deleteById(postId);
    }

}
