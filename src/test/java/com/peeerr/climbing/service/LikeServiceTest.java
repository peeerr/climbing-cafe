package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.Like;
import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.repository.LikeRepository;
import com.peeerr.climbing.repository.MemberRepository;
import com.peeerr.climbing.repository.PostRepository;
import com.peeerr.climbing.service.validator.LikeValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LikeValidator likeValidator;

    @Mock
    private RedisTemplate redisTemplate;

    @InjectMocks
    private LikeService likeService;

    @DisplayName("게시물 ID가 주어지면, 해당 게시물에 좋아요를 추가한다.")
    @Test
    void like() throws Exception {
        //given
        Long memberId = 1L;
        Long postId = 1L;

        Post post = Post.builder().id(postId).build();
        Member member = Member.builder().id(memberId).build();
        Like like = Like.builder()
                .memberId(member.getId())
                .postId(post.getId())
                .build();

        willDoNothing().given(likeValidator).validateLikeNotExists(anyLong(), anyLong());
        given(likeRepository.save(any(Like.class))).willReturn(like);

        ValueOperations<String, Long> valueOperations = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(anyString(), anyLong())).willReturn(1L);

        //when
        likeService.like(memberId, postId);

        //then
        then(likeValidator).should().validateLikeNotExists(memberId, postId);
        then(likeRepository).should().save(any(Like.class));

        then(redisTemplate).should().opsForValue();
        then(valueOperations).should().increment(anyString(), anyLong());
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

        ValueOperations<String, Long> valueOperations = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.decrement(anyString(), anyLong())).willReturn(1L);

        //when
        likeService.unlike(memberId, postId);

        //then
        then(likeRepository).should().findLikeByMemberIdAndPostId(anyLong(), anyLong());
        then(likeRepository).should().delete(any(Like.class));

        then(redisTemplate).should().opsForValue();
        then(valueOperations).should().decrement(anyString(), anyLong());
    }

    @DisplayName("해당 게시물 좋아요를 삭제하는데, 좋아요가 존재하지 않으면 예외를 던진다.")
    @Test
    void unlikeWithNonExistingLikeOrWithoutPermission() throws Exception {
        //given
        given(likeRepository.findLikeByMemberIdAndPostId(anyLong(), anyLong())).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(ClimbingException.class)
                .isThrownBy(() -> likeService.unlike(1L, 1L));

        then(likeRepository).should().findLikeByMemberIdAndPostId(anyLong(), anyLong());
    }

}
