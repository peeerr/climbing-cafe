package com.peeerr.climbing.service;

import com.peeerr.climbing.config.constant.MessageConstant;
import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.category.CategoryRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.dto.post.request.PostCreateRequest;
import com.peeerr.climbing.dto.post.request.PostEditRequest;
import com.peeerr.climbing.dto.post.response.PostResponse;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(Pageable pageable) {
        List<PostResponse> posts = postRepository.findAll(pageable).stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());

        return new PageImpl<>(posts, pageable, posts.size());
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        PostResponse postResponse = postRepository.findById(postId)
                .map(PostResponse::from)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstant.POST_NOT_FOUND));

        return postResponse;
    }

    @Transactional
    public PostResponse addPost(PostCreateRequest postCreateRequest) {
        Post post = Post.builder()
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .category(getCategory(postCreateRequest.getCategoryId()))
                .build();

        Post savedPost = postRepository.save(post);

        return PostResponse.from(savedPost);
    }

    @Transactional
    public PostResponse editPost(Long postId, PostEditRequest postEditRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstant.POST_NOT_FOUND));

        post.changeTitle(postEditRequest.getTitle());
        post.changeContent(postEditRequest.getContent());
        post.changeCategory(getCategory(postEditRequest.getCategoryId()));

        return PostResponse.from(post);
    }

    @Transactional
    public void removePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstant.POST_NOT_FOUND));

        postRepository.delete(post);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstant.CATEGORY_NOT_FOUND));
    }

}
