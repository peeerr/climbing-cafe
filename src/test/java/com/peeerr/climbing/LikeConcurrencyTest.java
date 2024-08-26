package com.peeerr.climbing;

import com.peeerr.climbing.domain.Category;
import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.repository.CategoryRepository;
import com.peeerr.climbing.repository.LikeRepository;
import com.peeerr.climbing.repository.MemberRepository;
import com.peeerr.climbing.repository.PostRepository;
import com.peeerr.climbing.service.LikeManager;
import com.peeerr.climbing.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@SpringBootTest
public class LikeConcurrencyTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeManager likeManager;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("[동시성 테스트] 10명의 사용자가 동시에 좋아요를 누른 후 좋아요 수는 10개가 되어야 한다. (초기: 0)")
    @Test
    public void concurrentLikeWithDifferentMembers() throws InterruptedException {
        // given
        int threadCount = 100;

        List<Member> members = createMembers(threadCount);
        Long postId = createPost().getId();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (Member member : members) {
            executorService.submit(() -> {
                try {
                    likeManager.like(member.getId(), postId);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Post updatedPost = postRepository.findById(postId).get();
        Long actualLikeCount = likeRepository.countLikeByPostId(updatedPost.getId());

        // likes 테이블에 저장된 좋아요 수와 Post 테이블의 해당 게시물의 likeCount
        System.out.println("actualLikeCount = " + actualLikeCount);
        System.out.println("updatedPost.getLikeCount() = " + updatedPost.getLikeCount());

        assertThat(actualLikeCount).isEqualTo(updatedPost.getLikeCount());
    }

    private List<Member> createMembers(int count) {
        List<Member> members = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            Member member = Member.builder()
                    .username("username__test" + i)
                    .email("test__" + i + "@example.com")
                    .password("password")
                    .build();
            memberRepository.save(member);
            members.add(member);
        }

        return members;
    }

    private Post createPost() {
        Category category = categoryRepository.save(Category.builder().categoryName("test__category__name").build());
        Member member = memberRepository.save(Member.builder().email("test__concurrency@example.com").password("password").username("test__concurrency").build());
        Post post = Post.builder().category(category).member(member).title("test title").content("test content").build();

        return postRepository.save(post);
    }

}
