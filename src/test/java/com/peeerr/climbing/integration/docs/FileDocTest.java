package com.peeerr.climbing.integration.docs;

import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.category.CategoryRepository;
import com.peeerr.climbing.domain.comment.CommentRepository;
import com.peeerr.climbing.domain.file.File;
import com.peeerr.climbing.domain.file.FileRepository;
import com.peeerr.climbing.domain.like.LikeRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
public class FileDocTest {

    @Autowired private FileRepository fileRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private LikeRepository likeRepository;

    @Autowired private PasswordEncoder passwordEncoder;
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

    @DisplayName("[통합 테스트/API 문서화] - 게시물 ID에 존재하는 모든 파일 URL 조회")
    @Test
    void fileUrlListByPost() throws Exception {
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

        MockMultipartFile file1 = new MockMultipartFile("files", "example1.jpg", "image/jpeg", "image1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "example2.jpg", "image/jpeg", "image2".getBytes());

        MemberPrincipal userDetails = new MemberPrincipal(member);
        Long postId = post.getId();

        mockMvc.perform(multipart("/api/posts/{postId}/files", postId)
                .file(file1)
                .file(file2)
                .with(user(userDetails))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        //when
        ResultActions result = mockMvc.perform(get("/api/posts/{postId}/files", postId)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("success"))
                .andDo(document("file-urls-for-post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("게시물 ID에 존재하는 모든 파일 URL")
                        )
                ));
    }

    @DisplayName("[통합 테스트/API 문서화] - 파일 업로드")
    @Test
    void fileUpload() throws Exception {
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

        MockMultipartFile file1 = new MockMultipartFile("files", "example1.jpg", "image/jpeg", "image1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "example2.jpg", "image/jpeg", "image2".getBytes());

        MemberPrincipal userDetails = new MemberPrincipal(member);
        Long postId = post.getId();

        //when
        ResultActions result = mockMvc.perform(multipart("/api/posts/{postId}/files", postId)
                .file(file1)
                .file(file2)
                .with(user(userDetails))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        //then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("message").value("success"))
                .andDo(document("file-upload",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID")
                        ),
                        requestParts(
                                partWithName("files").description("이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));
    }

    @DisplayName("[통합 테스트/API 문서화] - 파일 삭제 (유저 권한)")
    @Test
    void fileUpdateDeleteFlag() throws Exception {
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
        File file = fileRepository.save(
                File.builder()
                        .post(post)
                        .originalFilename("example1.jpg")
                        .filePath("/test")
                        .filename("UUID_example1")
                        .deleted(false)
                        .build()
        );

        MemberPrincipal userDetails = new MemberPrincipal(member);

        Long postId = post.getId();
        Long fileId = file.getId();

        //when
        ResultActions result = mockMvc.perform(delete("/api/posts/{postId}/files/{fileId}", postId, fileId)
                .with(user(userDetails)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("file-remove",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("postId").description("게시물 ID"),
                                parameterWithName("fileId").description("첨부파일 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));

        File savedFile = fileRepository.findAll().get(0);
        assertThat(savedFile.isDeleted()).isEqualTo(true);
    }

}
