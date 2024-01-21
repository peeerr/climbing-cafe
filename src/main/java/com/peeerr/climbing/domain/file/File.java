package com.peeerr.climbing.domain.file;

import com.peeerr.climbing.domain.BaseEntity;
import com.peeerr.climbing.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class File extends BaseEntity {

    @Column(name = "file_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filePath;

    private Boolean deleted;

    @PrePersist
    private void defaultDeleted() {
        this.deleted = false;
    }

    public void changeDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Builder
    public File(Post post, String originalFilename, String filename, String filePath) {
        this.post = post;
        this.originalFilename = originalFilename;
        this.filename = filename;
        this.filePath = filePath;
    }

}
