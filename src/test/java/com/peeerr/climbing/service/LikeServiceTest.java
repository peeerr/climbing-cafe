package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.like.Like;
import com.peeerr.climbing.domain.like.LikeRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import com.peeerr.climbing.exception.AlreadyExistsException;
import com.peeerr.climbing.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock private LikeRepository likeRepository;
    @Mock private PostRepository postRepository;
    @Mock private MemberRepository memberRepository;

    @InjectMocks
    private LikeService likeService;

    @DisplayName("해당 게시물에 있는 좋아요 수를 반환한다.")
    @Test
    void getLikeCount() throws Exception {
        //given
        Long postId = 1L;
        Post post = Post.builder().build();
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(likeRepository.countLikeByPost(post)).willReturn(5L);

        //when
        Long likeCount = likeService.getLikeCount(postId);

        //then
        assertThat(likeCount).isEqualTo(5L);

        then(postRepository).should().findById(postId);
        then(likeRepository).should().countLikeByPost(post);
    }

    @DisplayName("게시물 ID가 주어지면, 해당 게시물에 좋아요를 추가한다.")
    @Test
    void like() throws Exception {
        //given
        Long memberId = 1L;
        Long postId = 1L;

        Post post = Post.builder().build();
        Member member = Member.builder().build();
        Like like = Like.builder()
                .member(member)
                .post(post)
                .build();

        given(likeRepository.existsLikeByMemberIdAndPostId(anyLong(), anyLong())).willReturn(false);
        given(likeRepository.save(any(Like.class))).willReturn(like);
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        //when
        likeService.like(memberId, postId);

        //then
        then(likeRepository).should().existsLikeByMemberIdAndPostId(anyLong(), anyLong());
        then(likeRepository).should().save(any(Like.class));
        then(postRepository).should().findById(anyLong());
        then(memberRepository).should().findById(anyLong());
    }

    @DisplayName("좋아요를 추가하는데, 해당 게시물에 좋아요가 이미 존재하면 예외를 던진다.")
    @Test
    void likeWithAlreadyExistingLike() throws Exception {
        //given
        given(likeRepository.existsLikeByMemberIdAndPostId(anyLong(), anyLong())).willReturn(true);

        //when & then
        assertThrows(AlreadyExistsException.class, () -> likeService.like(1L, 1L));

        then(likeRepository.existsLikeByMemberIdAndPostId(anyLong(), anyLong()));
    }

    @DisplayName("게시물 ID가 주어지면, 해당 게시물 좋아요를 삭제한다.")
    @Test
    void unlike() throws Exception {
        //given
        Long memberId = 1L;
        Long postId = 1L;
        Like like = Like.builder().build();

        given(likeRepository.findLikeByMemberIdAndPostId(anyLong(), anyLong())).willReturn(Optional.of(like));
        willDoNothing().given(likeRepository).delete(any(Like.class));

        //when
        likeService.unlike(memberId, postId);

        //then
        then(likeRepository).should().findLikeByMemberIdAndPostId(anyLong(), anyLong());
        then(likeRepository).should().delete(any(Like.class));
    }

    @DisplayName("해당 게시물 좋아요를 삭제하는데, 좋아요가 존재하지 않으면 예외를 던진다.")
    @Test
    void unlikeWithNonExistingLikeOrWithoutPermission() throws Exception {
        //given
        given(likeRepository.findLikeByMemberIdAndPostId(anyLong(), anyLong())).willReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> likeService.unlike(1L, 1L));

        then(likeRepository).should().findLikeByMemberIdAndPostId(anyLong(), anyLong());
    }

}
