package com.peeerr.climbing.domain;

import com.peeerr.climbing.exception.ClimbingException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.peeerr.climbing.exception.ErrorCode.FILE_NOT_FOUND;

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

    @Column(nullable = false)
    private boolean deleted;

    @PrePersist
    private void defaultDeleted() {
        this.deleted = false;
    }

    public void checkNotDeleted() {
        if (this.isDeleted()) {
            throw new ClimbingException(FILE_NOT_FOUND);
        }
    }

    public void delete() {
        this.deleted = true;
    }

    @Builder
    public File(Post post, String originalFilename, String filename, String filePath, boolean deleted) {
        this.post = post;
        this.originalFilename = originalFilename;
        this.filename = filename;
        this.filePath = filePath;
        this.deleted = deleted;
    }

}
