package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.Comment;
import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.dto.request.CommentCreateRequest;
import com.peeerr.climbing.dto.request.CommentEditRequest;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.repository.CommentRepository;
import com.peeerr.climbing.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    @DisplayName("댓글 하나를 저장한다.")
    @Test
    void addComment() throws Exception {
        //given
        Long postId = 1L;
        Long parentId = 1L;

        CommentCreateRequest request = CommentCreateRequest.of(parentId, "댓글 테스트");
        Member member = Member.builder().build();

        given(postRepository.findById(postId)).willReturn(Optional.of(Post.builder().build()));
        given(commentRepository.findById(parentId)).willReturn(Optional.of(Comment.builder().build()));
        given(commentRepository.save(any(Comment.class))).willReturn(Comment.builder().build());

        //when
        commentService.addComment(postId, request, member);

        //then
        then(postRepository).should().findById(postId);
        then(commentRepository).should().findById(parentId);
        then(commentRepository).should().save(any(Comment.class));
    }

    @DisplayName("댓글 하나를 저장하는데, 저장할 게시물이 존재하지 않으면 예외를 던진다.")
    @Test
    void addCommentWithNonExistingPost() throws Exception {
        //given
        Long postId = 1L;
        Long parentId = 1L;

        CommentCreateRequest request = CommentCreateRequest.of(parentId, "댓글 테스트");
        Member member = Member.builder().build();

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(ClimbingException.class)
                .isThrownBy(() -> commentService.addComment(postId, request, member));

        then(postRepository).should().findById(postId);
    }

    @DisplayName("댓글 하나를 저장할 때, 부모 댓글을 조회하는데 존재하지 않으면 예외를 던진다.")
    @Test
    void addCommentWithNonExistingParentComment() throws Exception {
        //given
        Long postId = 1L;
        Long parentId = 1L;

        CommentCreateRequest request = CommentCreateRequest.of(parentId, "댓글 테스트");
        Member member = Member.builder().build();

        given(postRepository.findById(postId)).willReturn(Optional.of(Post.builder().build()));
        given(commentRepository.findById(parentId)).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(ClimbingException.class)
                .isThrownBy(() -> commentService.addComment(postId, request, member));

        then(postRepository).should().findById(postId);
        then(commentRepository).should().findById(parentId);
    }

    @DisplayName("댓글 하나를 수정한다.")
    @Test
    void editComment() throws Exception {
        //given
        Long commentId = 1L;
        Long loginId = 1L;
        CommentEditRequest request = CommentEditRequest.of("댓글 수정 테스트");

        Comment comment = Comment.builder()
                .member(Member.builder().id(loginId).build())
                .build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        //when
        commentService.editComment(commentId, request, loginId);

        //then
        then(commentRepository).should().findById(commentId);
    }

    @DisplayName("댓글 하나를 수정하는데, 수정할 댓글이 존재하지 않으면 예외를 던진다.")
    @Test
    void editCommentWithNonExistingComment() throws Exception {
        //given
        Long commentId = 1L;
        Long loginId = 1L;
        CommentEditRequest request = CommentEditRequest.of("댓글 수정 테스트");

        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(ClimbingException.class)
                .isThrownBy(() -> commentService.editComment(commentId, request, loginId));

        //then
        then(commentRepository).should().findById(commentId);
    }

    @DisplayName("[접근 권한X] 댓글 하나를 수정하는데, 로그인 사용자와 댓글 작성자가 다르면 예외를 던진다.")
    @Test
    void editCommentWithoutPermission() throws Exception {
        //given
        Long commentId = 1L;
        Long loginId = 1L;
        CommentEditRequest request = CommentEditRequest.of("댓글 수정 테스트");

        Comment comment = Comment.builder()
                .member(Member.builder().id(2L).build())
                .build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        //when & then
        assertThatExceptionOfType(ClimbingException.class)
                .isThrownBy(() -> commentService.editComment(commentId, request, loginId));

        then(commentRepository).should().findById(commentId);
    }

    @DisplayName("댓글 하나를 삭제한다.")
    @Test
    void removeComment() throws Exception {
        //given
        Long commentId = 1L;
        Long loginId = 1L;

        Comment comment = Comment.builder()
                .member(Member.builder().id(loginId).build())
                .build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        willDoNothing().given(commentRepository).delete(comment);

        //when
        commentService.removeComment(commentId, loginId);

        //then
        then(commentRepository).should().findById(commentId);
        then(commentRepository).should().delete(comment);
    }

    @DisplayName("댓글 하나를 삭제하는데, 삭제할 댓글이 존재하지 않으면 예외를 던진다.")
    @Test
    void removeCommentWithNonExistingComment() throws Exception {
        //given
        Long commentId = 1L;
        Long loginId = 1L;

        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(ClimbingException.class)
                .isThrownBy(() -> commentService.removeComment(commentId, loginId));

        //then
        then(commentRepository).should().findById(commentId);
    }

    @DisplayName("[접근 권한X] 댓글 하나를 삭제하는데 로그인 사용자와 댓글 작성자가 다르면 예외를 던진다.")
    @Test
    void removeCommentWithoutPermission() throws Exception {
        //given
        Long commentId = 1L;
        Long loginId = 1L;

        Comment comment = Comment.builder()
                .member(Member.builder().id(2L).build())
                .build();

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        //when & then
        assertThatExceptionOfType(ClimbingException.class)
                .isThrownBy(() -> commentService.removeComment(commentId, loginId));

        then(commentRepository).should().findById(commentId);
    }

}
