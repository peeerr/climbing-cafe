package com.peeerr.climbing.service;

import com.peeerr.climbing.dto.request.PostCreateRequest;
import com.peeerr.climbing.dto.request.PostEditRequest;
import com.peeerr.climbing.dto.request.PostSearchCondition;
import com.peeerr.climbing.dto.response.PostDetailResponse;
import com.peeerr.climbing.dto.response.PostResponse;
import com.peeerr.climbing.entity.Category;
import com.peeerr.climbing.entity.Member;
import com.peeerr.climbing.entity.Post;
import com.peeerr.climbing.exception.AccessDeniedException;
import com.peeerr.climbing.exception.notfound.CategoryNotFoundException;
import com.peeerr.climbing.exception.notfound.PostNotFoundException;
import com.peeerr.climbing.repository.CategoryRepository;
import com.peeerr.climbing.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition, Pageable pageable) {
        return postRepository.findPostsFilteredByCategoryIdAndSearchWord(categoryId, condition, pageable);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostWithComments(Long postId) {
        return postRepository.findPostById(postId)
                .map(PostDetailResponse::from)
                .orElseThrow(PostNotFoundException::new);
    }

    @Transactional
    public void addPost(PostCreateRequest postCreateRequest, Member member) {
        Post post = Post.builder()
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .category(getCategory(postCreateRequest.getCategoryId()))
                .member(member)
                .build();

        postRepository.save(post);
    }

    @Transactional
    public void editPost(Long postId, PostEditRequest postEditRequest, Long loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (!post.getMember().getId().equals(loginId)) {
            throw new AccessDeniedException();
        }

        post.changeTitle(postEditRequest.getTitle());
        post.changeContent(postEditRequest.getContent());
        post.changeCategory(getCategory(postEditRequest.getCategoryId()));
    }

    @Transactional
    public void removePost(Long postId, Long loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (!post.getMember().getId().equals(loginId)) {
            throw new AccessDeniedException();
        }

        postRepository.delete(post);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
    }

}
