package com.peeerr.climbing.integration.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.config.auth.CustomUserDetails;
import com.peeerr.climbing.domain.category.CategoryRepository;
import com.peeerr.climbing.domain.comment.CommentRepository;
import com.peeerr.climbing.domain.file.FileRepository;
import com.peeerr.climbing.domain.like.LikeRepository;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import com.peeerr.climbing.dto.member.MemberCreateRequest;
import com.peeerr.climbing.dto.member.MemberEditRequest;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "climbing.com", uriPort = 80)
@ExtendWith(RestDocumentationExtension.class)
public class MemberDocTest {

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

    @DisplayName("[통합 테스트/API 문서화] - 회원가입")
    @Test
    void memberAdd() throws Exception {
        //given
        MemberCreateRequest request = MemberCreateRequest.of("test", "test1234", "test1234", "test@example.com");

        //when
        ResultActions result = mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("member-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("이메일 아이디"),
                                fieldWithPath("username").description("닉네임"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("checkPassword").description("비밀번호 확인")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));
    }

    @DisplayName("[통합 테스트/API 문서화] - 회원정보 변경")
    @Test
    void memberEdit() throws Exception {
        //given
        Member member = memberRepository.save(
                Member.builder()
                        .username("test")
                        .password(passwordEncoder.encode("test1234"))
                        .email("test@example.com")
                        .build()
        );

        CustomUserDetails userDetails = new CustomUserDetails(member);

        MemberEditRequest request = MemberEditRequest.of("editTest", "editTest@example.com");

        //when
        ResultActions result = mockMvc.perform(put("/api/members/{memberId}", member.getId())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(document("member-edit",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("회원 ID")
                        ),
                        requestFields(
                                fieldWithPath("username").description("닉네임"),
                                fieldWithPath("email").description("이메일 아이디")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));
    }

}
