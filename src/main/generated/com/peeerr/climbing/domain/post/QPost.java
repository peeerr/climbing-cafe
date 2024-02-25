package com.peeerr.climbing.domain.post;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = -1263733799L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final com.peeerr.climbing.domain.QBaseEntity _super = new com.peeerr.climbing.domain.QBaseEntity(this);

    public final com.peeerr.climbing.domain.category.QCategory category;

    public final ListPath<com.peeerr.climbing.domain.comment.Comment, com.peeerr.climbing.domain.comment.QComment> comments = this.<com.peeerr.climbing.domain.comment.Comment, com.peeerr.climbing.domain.comment.QComment>createList("comments", com.peeerr.climbing.domain.comment.Comment.class, com.peeerr.climbing.domain.comment.QComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final ListPath<com.peeerr.climbing.domain.file.File, com.peeerr.climbing.domain.file.QFile> files = this.<com.peeerr.climbing.domain.file.File, com.peeerr.climbing.domain.file.QFile>createList("files", com.peeerr.climbing.domain.file.File.class, com.peeerr.climbing.domain.file.QFile.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.peeerr.climbing.domain.user.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final StringPath title = createString("title");

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.peeerr.climbing.domain.category.QCategory(forProperty("category")) : null;
        this.member = inits.isInitialized("member") ? new com.peeerr.climbing.domain.user.QMember(forProperty("member")) : null;
    }

}

