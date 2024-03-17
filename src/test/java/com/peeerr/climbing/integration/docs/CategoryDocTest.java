package com.peeerr.climbing.integration.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.category.CategoryRepository;
import com.peeerr.climbing.dto.category.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.category.request.CategoryEditRequest;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "climbing.com", uriPort = 80)
@ExtendWith(RestDocumentationExtension.class)
public class CategoryDocTest {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ObjectMapper mapper;
    @Autowired private MockMvc mockMvc;

    @BeforeEach
    public void cleanup() {
        categoryRepository.deleteAll();
    }

    @DisplayName("[통합 테스트/API 문서화] - 모든 게시판 조회")
    @Test
    void categoryList() throws Exception {
        //given
        String categoryName1 = "자유 게시판";
        String categoryName2 = "후기 게시판";
        String categoryName3 = "자랑 게시판";

        categoryRepository.saveAll(
                List.of(
                        Category.builder()
                            .categoryName(categoryName1)
                            .build(),
                        Category.builder()
                            .categoryName(categoryName2)
                            .build(),
                        Category.builder()
                            .categoryName(categoryName3)
                            .build()
                )
        );
        
        //when
        ResultActions result = mockMvc.perform(get("/api/categories")
                .accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("success"))
                .andExpect(jsonPath("data[0].categoryName").value(categoryName1))
                .andExpect(jsonPath("data[1].categoryName").value(categoryName2))
                .andExpect(jsonPath("data[2].categoryName").value(categoryName3))
                .andDo(document("category-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data[*].categoryId").description("게시판 ID"),
                                fieldWithPath("data[*].categoryName").description("게시판 이름")
                        )
                ));
    }
    
    @DisplayName("[통합 테스트/API 문서화] - 게시판 생성")
    @Test
    void categoryAdd() throws Exception {
        //given
        CategoryCreateRequest request = CategoryCreateRequest.of("자유 게시판");

        //when
        ResultActions result = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("message").value("success"))
                .andDo(document("category-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("categoryName").description("게시판 이름")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));
    }

    @DisplayName("[통합 테스트/API 문서화] - 게시판 이름 변경")
    @Test
    void categoryEdit() throws Exception {
        //given
        String categoryName = "자유 게시판";

        Category category = categoryRepository.save(
                Category.builder()
                        .categoryName(categoryName)
                        .build()
        );

        CategoryEditRequest request = CategoryEditRequest.of("후기 게시판");

        Long categoryId = category.getId();

        //when
        ResultActions result = mockMvc.perform(put("/api/categories/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("success"))
                .andDo(document("category-edit",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("categoryId").description("게시판 ID")
                        ),
                        requestFields(
                                fieldWithPath("categoryName").description("게시판 이름")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));

        Category categoryEntity = categoryRepository.findAll().get(0);
        assertThat(categoryEntity.getCategoryName()).isEqualTo(request.getCategoryName());
    }

    @DisplayName("[통합 테스트/API 문서화] - 게시판 삭제")
    @Test
    void categoryRemove() throws Exception {
        //given
        String categoryName = "자유 게시판";

        Category category = categoryRepository.save(
                Category.builder()
                        .categoryName(categoryName)
                        .build()
        );

        Long categoryId = category.getId();

        //when
        ResultActions result = mockMvc.perform(delete("/api/categories/{categoryId}", categoryId)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("success"))
                .andDo(document("category-remove",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("categoryId").description("게시판 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("결과 메시지"),
                                fieldWithPath("data").description("")
                        )
                ));
    }

}
