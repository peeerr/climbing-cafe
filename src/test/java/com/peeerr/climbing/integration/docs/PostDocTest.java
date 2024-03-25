package com.peeerr.climbing.integration.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.config.auth.CustomUserDetails;
import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.category.CategoryRepository;
import com.peeerr.climbing.domain.comment.CommentRepository;
import com.peeerr.climbing.domain.file.FileRepository;
import com.peeerr.climbing.domain.like.LikeRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import com.peeerr.climbing.dto.post.PostCreateRequest;
import com.peeerr.climbing.dto.post.PostEditRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "climbing.com", uriPort = 80)
@ExtendWith(RestDocumentationExtension.class)
public class PostDocTest {

    @Autowired private FileRepository fileRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private LikeRepository likeRepository;

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper mapper;
    @Autowired private MockMvc mockMvc;

    @BeforeEach
    public void cleanup() {
        fileRepository.deleteAll();
        commentRepository.deleteAll();
        likeRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @DisplayName("[통합 테스트/API 문서화] - 글 전체 조회 (필터 By 카테고리 and 검색어")
    @Test
    void postList() throws Exception {
        //given
        Long categoryId = null;

        for (int i = 1; i <= 5; i++) {
            Member member = memberRepository.save(
                    Member.builder()
                            .username("test" + i)
                            .password(passwordEncoder.encode("test1234" + i))
                            .email("test" + i + "@example.com")
                            .build()
            );
            Category category = categoryRepository.save(
                    Category.builder()
                            .categoryName("게시판" + i)
                            .build()
            );
            postRepository.save(
                    Post.builder()
                            .category(category)
                            .member(member)
                            .title("제목 테스트" + i)
                            .content("본문 테스트" + i)
                            .build()
            );

            categoryId = category.getId();
        }

        //when
        ResultActions result = mockMvc.perform(get("/api/posts")
                .queryParam("categoryId", categoryId.toString())
                .queryParam("title", "테스트")
                .queryParam("content", "테스트")
                .accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("post-list-filtered",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("categoryId").description("게사판 ID"),
                                parameterWithName("title").description("게시물 제목으로 필터링"),
                                parameterWithName("content").description("게시물 본문으로 필터링")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data.content[*].postId").description("게시물 ID"),
                                fieldWithPath("data.content[*].categoryName").description("게시물이 속한 게시판 이름"),
                                fieldWithPath("data.content[*].writer").description("작성자"),
                                fieldWithPath("data.content[*].createDate").description("생성일시"),
                                fieldWithPath("data.content[*].modifyDate").description("수정일시"),
                                fieldWithPath("data.pageable.pageNumber").description("현재 페이지 번호"),
                                fieldWithPath("data.pageable.pageSize").description("페이지 당 게시물 수"),
                                fieldWithPath("data.pageable.sort.empty").description("정렬이 비어 있는지 여부"),
                                fieldWithPath("data.pageable.sort.unsorted").description("정렬이 적용되지 않았는지 여부"),
                                fieldWithPath("data.pageable.sort.sorted").description("정렬이 적용되었는지 여부"),
                                fieldWithPath("data.pageable.offset").description("현재 페이지의 첫 번째 요소의 번호"),
                                fieldWithPath("data.pageable.paged").description("페이지 처리가 적용되었는지 여부"),
                                fieldWithPath("data.pageable.unpaged").description("페이지 처리가 적용되지 않았는지 여부"),
                                fieldWithPath("data.last").description("현재 페이지가 마지막 페이지인지 여부"),
                                fieldWithPath("data.totalElements").description("전체 게시물의 수"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.size").description("페이지 당 게시물의 수"),
                                fieldWithPath("data.number").description("현재 페이지 번호"),
                                fieldWithPath("data.sort.empty").description("정렬이 비어 있는지 여부"),
                                fieldWithPath("data.sort.unsorted").description("정렬이 적용되지 않았는지 여부"),
                                fieldWithPath("data.sort.sorted").description("정렬이 적용되었는지 여부"),
                                fieldWithPath("data.numberOfElements").description("현재 페이지의 게시물 수"),
                                fieldWithPath("data.first").description("현재 페이지가 첫 번째 페이지인지 여부"),
                                fieldWithPath("data.empty").description("현재 페이지가 비어 있는지 여부")
                        )
                ));
    }

    @DisplayName("[통합 테스트/API 문서화] - 글 단건 조회")
    @Test
    void postDetail() throws Exception {
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

        Long postId = post.getId();

        //when
        ResultActions result = mockMvc.perform(get("/api/posts/{postId}", postId)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("post-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data.postId").description("게시물 ID"),
                                fieldWithPath("data.title").description("제목"),
                                fieldWithPath("data.content").description("본문 글"),
                                fieldWithPath("data.categoryName").description("게시물이 속한 게시판 이름"),
                                fieldWithPath("data.writer").description("작성자"),
                                fieldWithPath("data.filePaths").description("게시물에 첨부된 모든 파일 경로"),
                                fieldWithPath("data.comments").description("게시물에 달린 모든 댓글 정보"),
                                fieldWithPath("data.createDate").description("생성일시"),
                                fieldWithPath("data.modifyDate").description("수정일시")
                        )
                ));

        assertThat(post.getTitle()).isEqualTo("제목 테스트");
        assertThat(post.getContent()).isEqualTo("본문 테스트");
    }

    @DisplayName("[통합 테스트/API 문서화] - 게시물 등록")
    @Test
    void postAdd() throws Exception {
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

        CustomUserDetails userDetails = new CustomUserDetails(member);

        Long categoryId = category.getId();
        PostCreateRequest request = PostCreateRequest.of("제목 작성 테스트", "본문 작성 테스트", categoryId);

        //when
        ResultActions result = mockMvc.perform(post("/api/posts")
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("post-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("본문 글"),
                                fieldWithPath("categoryId").description("카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));
    }

    @DisplayName("[통합 테스트/API 문서화] - 게시물 수정")
    @Test
    void postEdit() throws Exception {
        //given
        Member member = memberRepository.save(
                Member.builder()
                        .username("test")
                        .password(passwordEncoder.encode("test1234"))
                        .email("test@example.com")
                        .build()
        );
        Category category1 = categoryRepository.save(
                Category.builder()
                        .categoryName("자유 게시판")
                        .build()
        );
        Long postId = postRepository.save(
                Post.builder()
                        .category(category1)
                        .member(member)
                        .title("제목 테스트")
                        .content("본문 테스트")
                        .build()
        ).getId();

        CustomUserDetails userDetails = new CustomUserDetails(member);
        Category editCategory = categoryRepository.save(
                Category.builder()
                        .categoryName("후기 게시판")
                        .build()
        );

        Long categoryId = editCategory.getId();
        PostEditRequest request = PostEditRequest.of("제목 수정 테스트", "본문 수정 테스트", categoryId);

        //when
        ResultActions result = mockMvc.perform(put("/api/posts/{postId}", postId)
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("post-edit",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID")
                        ),
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("본문 글"),
                                fieldWithPath("categoryId").description("카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));
    }

    @DisplayName("[통합 테스트/API 문서화] - 게시물 삭제")
    @Test
    void postRemove() throws Exception {
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
        Long postId = postRepository.save(
                Post.builder()
                        .category(category)
                        .member(member)
                        .title("제목 테스트")
                        .content("본문 테스트")
                        .build()
        ).getId();

        CustomUserDetails userDetails = new CustomUserDetails(member);

        //when
        ResultActions result = mockMvc.perform(delete("/api/posts/{postId}", postId)
                .with(user(userDetails)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("post-remove",
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

}
