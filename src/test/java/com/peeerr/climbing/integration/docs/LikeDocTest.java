package com.peeerr.climbing.integration.docs;

import com.peeerr.climbing.domain.Category;
import com.peeerr.climbing.domain.Like;
import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.repository.*;
import com.peeerr.climbing.security.MemberPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "climbing.com", uriPort = 80)
@ExtendWith(RestDocumentationExtension.class)
public class LikeDocTest {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void cleanup() {
        fileRepository.deleteAll();
        commentRepository.deleteAll();
        likeRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @DisplayName("[통합 테스트/API 문서화] - 좋아요 달기")
    @Test
    void likeAdd() throws Exception {
        //given
        Member member = memberRepository.save(
                Member.builder()
                        .username("test")
                        .password(passwordEncoder.encode("test1234"))
                        .email("test@example.com")
                        .build()
        );
        Category category = categoryRepository.save(
                Category.builder()
                        .categoryName("자유 게시판")
                        .build()
        );
        Post post = postRepository.save(
                Post.builder()
                        .category(category)
                        .member(member)
                        .title("제목 테스트")
                        .content("본문 테스트")
                        .build()
        );

        MemberPrincipal userDetails = new MemberPrincipal(member);
        Long postId = post.getId();

        //when
        ResultActions result = mockMvc.perform(post("/api/posts/{postId}/likes", postId)
                .with(user(userDetails)));

        //then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID")
                        )
                ));
    }

    @DisplayName("[통합 테스트/API 문서화] - 좋아요 취소")
    @Test
    void likeRemove() throws Exception {
        //given
        Member member = memberRepository.save(
                Member.builder()
                        .username("test")
                        .password(passwordEncoder.encode("test1234"))
                        .email("test@example.com")
                        .build()
        );
        Category category = categoryRepository.save(
                Category.builder()
                        .categoryName("자유 게시판")
                        .build()
        );
        Post post = postRepository.save(
                Post.builder()
                        .category(category)
                        .member(member)
                        .title("제목 테스트")
                        .content("본문 테스트")
                        .build()
        );
        likeRepository.save(
                Like.builder()
                        .memberId(member.getId())
                        .postId(post.getId())
                        .build()
        );

        long savedCount = likeRepository.count();

        MemberPrincipal userDetails = new MemberPrincipal(member);
        Long postId = post.getId();

        //when
        ResultActions result = mockMvc.perform(delete("/api/posts/{postId}/likes", postId)
                .with(user(userDetails)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("like-cancel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID")
                        )
                ));

        long deletedCount = likeRepository.count();

        assertThat(deletedCount).isEqualTo(savedCount - 1);
    }

}
