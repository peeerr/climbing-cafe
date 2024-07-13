package com.peeerr.climbing.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "parent_id", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parentComment = null;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> comments;

    @Column(nullable = false, length = 500)
    private String content;

    @Builder
    public Comment(Post post, Member member, Comment parentComment, String content) {
        this.post = post;
        this.member = member;
        this.parentComment = parentComment;
        this.content = content;
    }

    public void changeContent(String content) {
        this.content = content;
    }

}
