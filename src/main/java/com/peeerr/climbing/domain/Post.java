package com.peeerr.climbing.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.peeerr.climbing.exception.ClimbingException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.peeerr.climbing.exception.ErrorCode.ACCESS_DENIED;

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

    @JsonIgnoreProperties("posts")
    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private Long likeCount = 0L;

    @JsonIgnoreProperties("post")
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnoreProperties("post")
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    @JsonIgnoreProperties("post")
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @Builder
    private Post(Long id, String title, String content, Category category, Member member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.member = member;
    }

    public List<String> getFileNames() {
        return this.files.stream()
                .map(File::getFilename)
                .toList();
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

    public void checkOwner(Long loginId) {
        if (!this.member.getId().equals(loginId)) {
            throw new ClimbingException(ACCESS_DENIED);
        }
    }

    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    public void decreaseLikeCount() {
        this.likeCount -= 1;
    }

}
