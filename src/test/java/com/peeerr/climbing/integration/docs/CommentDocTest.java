package com.peeerr.climbing.integration.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.config.auth.CustomUserDetails;
import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.category.CategoryRepository;
import com.peeerr.climbing.domain.comment.Comment;
import com.peeerr.climbing.domain.comment.CommentRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import com.peeerr.climbing.dto.comment.request.CommentCreateRequest;
import com.peeerr.climbing.dto.comment.request.CommentEditRequest;
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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
public class CommentDocTest {

    @Autowired private CommentRepository commentRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private MemberRepository memberRepository;

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper mapper;
    @Autowired private MockMvc mockMvc;

    @BeforeEach
    public void cleanup() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        memberRepository.deleteAll();
    }
    
    @DisplayName("[통합 테스트/API 문서화] - 댓글 작성")
    @Test
    void commentAdd() throws Exception {
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

        CustomUserDetails userDetails = new CustomUserDetails(member);
        CommentCreateRequest request = CommentCreateRequest.of(null, "댓글 테스트");
        Long postId = post.getId();

        //when
        ResultActions result = mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("comment-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID")
                        ),
                        requestFields(
                                fieldWithPath("parentId").description("부모 댓글 ID"),
                                fieldWithPath("content").description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));
    }

    @DisplayName("[통합 테스트/API 문서화] - 댓글 수정")
    @Test
    void commentEdit() throws Exception {
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
        Comment comment = commentRepository.save(
                Comment.builder()
                        .member(member)
                        .post(post)
                        .content("댓글 테스트")
                        .build()
        );

        CustomUserDetails userDetails = new CustomUserDetails(member);
        CommentEditRequest request = CommentEditRequest.of("댓글 수정 테스트");
        Long postId = post.getId();
        Long commentId = comment.getId();

        //when
        ResultActions result = mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("comment-edit",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID"),
                                parameterWithName("commentId").description("댓글 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));
    }

    @DisplayName("[통합 테스트/API 문서화] - 댓글 삭제")
    @Test
    void commentRemove() throws Exception {
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
        Comment comment = commentRepository.save(
                Comment.builder()
                        .member(member)
                        .post(post)
                        .content("댓글 테스트")
                        .build()
        );

        CustomUserDetails userDetails = new CustomUserDetails(member);
        Long postId = post.getId();
        Long commentId = comment.getId();

        //when
        ResultActions result = mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                .with(user(userDetails))
                .accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("comment-remove",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID"),
                                parameterWithName("commentId").description("댓글 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));
    }

}
