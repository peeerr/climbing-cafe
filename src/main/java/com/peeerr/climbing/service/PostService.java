package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.Category;
import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.dto.request.PostCreateRequest;
import com.peeerr.climbing.dto.request.PostEditRequest;
import com.peeerr.climbing.dto.request.PostSearchCondition;
import com.peeerr.climbing.dto.response.PopularPostResponse;
import com.peeerr.climbing.dto.response.PostDetailResponse;
import com.peeerr.climbing.dto.response.PostResponse;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.CategoryRepository;
import com.peeerr.climbing.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findPostById(postId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.POST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition, Pageable pageable) {
        return postRepository.findPostsFilteredByCategoryIdAndSearchWord(categoryId, condition, pageable);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long postId) {
        Post post = getPostById(postId);

        return PostDetailResponse.from(post);
    }

    @Transactional(readOnly = true)
    public List<PopularPostResponse> getPopularPosts() {
        return postRepository.findPopularPosts();
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

    public void editPost(Long postId, PostEditRequest request, Long loginId) {
        Post post = getPostById(postId);
        post.checkOwner(loginId);

        post.changeTitle(request.getTitle());
        post.changeContent(request.getContent());
        post.changeCategory(getCategory(request.getCategoryId()));
    }

    public void removePost(Long postId, Long loginId) {
        Post post = getPostById(postId);
        post.checkOwner(loginId);

        postRepository.delete(post);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.CATEGORY_NOT_FOUND));
    }

}
