package com.peeerr.climbing.domain.category;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategory is a Querydsl query type for Category
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategory extends EntityPathBase<Category> {

    private static final long serialVersionUID = -1718928487L;

    public static final QCategory category = new QCategory("category");

    public final com.peeerr.climbing.domain.QBaseEntity _super = new com.peeerr.climbing.domain.QBaseEntity(this);

    public final StringPath categoryName = createString("categoryName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final ListPath<com.peeerr.climbing.domain.post.Post, com.peeerr.climbing.domain.post.QPost> posts = this.<com.peeerr.climbing.domain.post.Post, com.peeerr.climbing.domain.post.QPost>createList("posts", com.peeerr.climbing.domain.post.Post.class, com.peeerr.climbing.domain.post.QPost.class, PathInits.DIRECT2);

    public QCategory(String variable) {
        super(Category.class, forVariable(variable));
    }

    public QCategory(Path<? extends Category> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCategory(PathMetadata metadata) {
        super(Category.class, metadata);
    }

}

