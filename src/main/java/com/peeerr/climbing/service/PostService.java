package com.peeerr.climbing.service;

import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.domain.Category;
import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.dto.request.PostCreateRequest;
import com.peeerr.climbing.dto.request.PostEditRequest;
import com.peeerr.climbing.dto.request.PostSearchCondition;
import com.peeerr.climbing.dto.response.PostDetailResponse;
import com.peeerr.climbing.dto.response.PostResponse;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.repository.CategoryRepository;
import com.peeerr.climbing.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
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
                .orElseThrow(() -> new ClimbingException(ErrorMessage.POST_NOT_FOUND));
    }

    public void addPost(PostCreateRequest postCreateRequest, Member member) {
        Post post = Post.builder()
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .category(getCategory(postCreateRequest.getCategoryId()))
                .member(member)
                .build();

        postRepository.save(post);
    }

    public void editPost(Long postId, PostEditRequest postEditRequest, Long loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ClimbingException(ErrorMessage.POST_NOT_FOUND));

        checkOwner(loginId, post.getMember().getId());

        post.changeTitle(postEditRequest.getTitle());
        post.changeContent(postEditRequest.getContent());
        post.changeCategory(getCategory(postEditRequest.getCategoryId()));
    }

    public void removePost(Long postId, Long loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ClimbingException(ErrorMessage.POST_NOT_FOUND));

        checkOwner(loginId, post.getMember().getId());

        postRepository.delete(post);
    }

    private void checkOwner(Long loginId, Long ownerId) {
        if (!loginId.equals(ownerId)) {
            throw new ClimbingException(ErrorMessage.ACCESS_DENIED);
        }
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ClimbingException(ErrorMessage.CATEGORY_NOT_FOUND));
    }

}
