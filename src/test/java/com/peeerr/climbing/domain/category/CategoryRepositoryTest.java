package com.peeerr.climbing.domain.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired private CategoryRepository categoryRepository;

    @DisplayName("카테고리 하나를 저장한다.")
    @Test
    void save() throws Exception {
        //given
        String categoryName = "자유 게시판";
        Category category = Category.builder().categoryName(categoryName).build();

        //when
        Category savedCategory = categoryRepository.save(category);

        //then
        assertThat(savedCategory.getCategoryName()).isEqualTo(categoryName);
    }

    @DisplayName("카테고리를 전체 조회해 온다.")
    @Test
    void findAll() throws Exception {
        //given
        String categoryName1 = "자유 게시판";
        String categoryName2 = "후기 게시판";
        String categoryName3 = "인기 게시판";

        Category category1 = Category.builder().categoryName(categoryName1).build();
        Category category2 = Category.builder().categoryName(categoryName2).build();
        Category category3 = Category.builder().categoryName(categoryName3).build();

        categoryRepository.saveAll(List.of(category1, category2, category3));

        //when
        List<Category> categories = categoryRepository.findAll();

        //then
        assertThat(categories).hasSize(3)
                .extracting("id", "categoryName")
                .containsExactlyInAnyOrder(
                        tuple(1L, categoryName1),
                        tuple(2L, categoryName2),
                        tuple(3L, categoryName3)
                );
    }

    @DisplayName("id로 카테고리 하나를 조회해 온다.")
    @Test
    void findById() throws Exception {
        //given
        String categoryName = "자유 게시판";
        Category category = Category.builder().categoryName(categoryName).build();

        Category savedCategory = categoryRepository.save(category);
        Long categoryId = savedCategory.getId();

        //when
        Category foundCategory = categoryRepository.findById(categoryId).orElseThrow();

        //then
        assertThat(foundCategory.getId()).isEqualTo(categoryId);
        assertThat(foundCategory.getCategoryName()).isEqualTo(savedCategory.getCategoryName());
    }

    @DisplayName("id에 해당하는 카테고리를 삭제한다.")
    @Test
    void deleteById() throws Exception {
        //given
        String categoryName = "자유 게시판";
        Category category = Category.builder().categoryName(categoryName).build();

        Long categoryId = categoryRepository.save(category).getId();
        long count = categoryRepository.count();

        //when
        categoryRepository.deleteById(categoryId);

        //then
        assertThat(categoryRepository.count()).isEqualTo(count - 1);
    }

}