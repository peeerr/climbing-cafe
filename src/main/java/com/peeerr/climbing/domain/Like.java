package com.peeerr.climbing.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(
                name = "like_uk",
                columnNames = {"member_id", "post_id"}
        )
})
@Entity
public class Like extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Builder
    public Like(Member member, Post post) {
        this.member = member;
        this.post = post;
    }

}
