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

    private Long memberId;
    private Long postId;

    @Builder
    public Like(Long memberId, Long postId) {
        this.memberId = memberId;
        this.postId = postId;
    }

}
