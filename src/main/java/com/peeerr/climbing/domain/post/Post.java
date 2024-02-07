package com.peeerr.climbing.domain.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.peeerr.climbing.domain.BaseEntity;
import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.file.File;
import com.peeerr.climbing.domain.user.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseEntity {

    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @JsonIgnoreProperties("posts")
    @JoinColumn(name = "category_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("posts")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JsonIgnoreProperties("post")
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<File> file = new ArrayList<>();

    @Builder
    private Post(String title, String content, Category category, Member member) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.member = member;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeCategory(Category category) {
        this.category = category;
    }

}
