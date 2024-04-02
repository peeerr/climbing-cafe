package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.category.CategoryRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.dto.post.PostCreateRequest;
import com.peeerr.climbing.dto.post.PostEditRequest;
import com.peeerr.climbing.dto.post.PostSearchCondition;
import com.peeerr.climbing.dto.post.PostResponse;
import com.peeerr.climbing.dto.post.PostDetailResponse;
import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.exception.EntityNotFoundException;
import com.peeerr.climbing.exception.UnauthorizedAccessException;
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

    /*
     * 게시판, 검색어로 필터링된 모든 게시물 조회
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition, Pageable pageable) {
        return postRepository.findPostsFilteredByCategoryIdAndSearchWord(categoryId, condition, pageable);
    }

    /*
     * 게시물 상세조회
     */
    @Transactional(readOnly = true)
    public PostDetailResponse getPostWithComments(Long postId) {
        return postRepository.findPostById(postId)
                .map(PostDetailResponse::from)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.POST_NOT_FOUND));
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
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(loginId)) {
            throw new UnauthorizedAccessException(ErrorMessage.NO_ACCESS_PERMISSION);
        }

        post.changeTitle(postEditRequest.getTitle());
        post.changeContent(postEditRequest.getContent());
        post.changeCategory(getCategory(postEditRequest.getCategoryId()));
    }

    @Transactional
    public void removePost(Long postId, Long loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(loginId)) {
            throw new UnauthorizedAccessException(ErrorMessage.NO_ACCESS_PERMISSION);
        }

        postRepository.delete(post);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));
    }

}
