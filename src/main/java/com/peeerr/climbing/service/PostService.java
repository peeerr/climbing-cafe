package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.category.CategoryRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.dto.post.request.PostCreateRequest;
import com.peeerr.climbing.dto.post.request.PostEditRequest;
import com.peeerr.climbing.dto.post.request.PostSearchCondition;
import com.peeerr.climbing.dto.post.response.PostResponse;
import com.peeerr.climbing.dto.post.response.PostWithCommentsResponse;
import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import com.peeerr.climbing.exception.ex.UnauthorizedAccessException;
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
    public Page<PostResponse> getPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition, Pageable pageable) {
        List<PostResponse> posts = postRepository.getPostsFilteredByCategoryIdAndSearchWord(categoryId, condition).stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());

        return new PageImpl<>(posts, pageable, posts.size());
    }

    @Transactional(readOnly = true)
    public PostWithCommentsResponse getPostWithComments(Long postId) {
        return postRepository.findById(postId)
                .map(PostWithCommentsResponse::from)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.POST_NOT_FOUND));
    }

    @Transactional
    public PostResponse addPost(PostCreateRequest postCreateRequest, Member member) {
        Post post = Post.builder()
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .category(getCategory(postCreateRequest.getCategoryId()))
                .member(member)
                .build();

        Post savedPost = postRepository.save(post);

        return PostResponse.from(savedPost);
    }

    @Transactional
    public PostResponse editPost(Long postId, PostEditRequest postEditRequest, Long loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(loginId)) {
            throw new UnauthorizedAccessException(ErrorMessage.NO_ACCESS_PERMISSION);
        }

        post.changeTitle(postEditRequest.getTitle());
        post.changeContent(postEditRequest.getContent());
        post.changeCategory(getCategory(postEditRequest.getCategoryId()));

        return PostResponse.from(post);
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
