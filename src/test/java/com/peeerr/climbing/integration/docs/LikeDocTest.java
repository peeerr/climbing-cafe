package com.peeerr.climbing.integration.docs;

import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.domain.Category;
import com.peeerr.climbing.repository.CategoryRepository;
import com.peeerr.climbing.repository.CommentRepository;
import com.peeerr.climbing.repository.FileRepository;
import com.peeerr.climbing.domain.Like;
import com.peeerr.climbing.repository.LikeRepository;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.repository.PostRepository;
import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @BeforeEach
    public void cleanup() {
        fileRepository.deleteAll();
        commentRepository.deleteAll();
        likeRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @DisplayName("[통합 테스트/API 문서화] - 게시물에 달린 좋아요 개수 조회")
    @Test
    void likeCount() throws Exception {
        //given
        for (int i = 0; i < 3; i++) {
            memberRepository.save(
                    Member.builder()
                            .username("test" + i)
                            .password(passwordEncoder.encode("test1234"))
                            .email("test" + i + "@example.com")
                            .build()
            );
        }
        Category category = categoryRepository.save(
                Category.builder()
                        .categoryName("자유 게시판")
                        .build()
        );

        List<Member> members = memberRepository.findAll();

        Post post = postRepository.save(
                Post.builder()
                        .category(category)
                        .member(members.get(0))
                        .title("제목 테스트")
                        .content("본문 테스트")
                        .build()
        );
        likeRepository.saveAll(
                List.of(
                        Like.builder()
                                .member(members.get(0))
                                .post(post)
                                .build(),
                        Like.builder()
                                .member(members.get(1))
                                .post(post)
                                .build(),
                        Like.builder()
                                .member(members.get(2))
                                .post(post)
                                .build()
                )
        );

        Long postId = post.getId();

        //when
        ResultActions result = mockMvc.perform(get("/api/posts/{postId}/likes/count", postId)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("3"))
                .andDo(document("like-count",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("게시물에 달린 좋아요 개수")
                        )
                ));
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
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
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
                        .member(member)
                        .post(post)
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
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("like-cancel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));

        long deletedCount = likeRepository.count();

        assertThat(deletedCount).isEqualTo(savedCount - 1);
    }

}
