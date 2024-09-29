package com.peeerr.climbing.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileUploadStatus is a Querydsl query type for FileUploadStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFileUploadStatus extends EntityPathBase<FileUploadStatus> {

    private static final long serialVersionUID = -1667369948L;

    public static final QFileUploadStatus fileUploadStatus = new QFileUploadStatus("fileUploadStatus");

    public final StringPath fileId = createString("fileId");

    public final EnumPath<FileUploadState> state = createEnum("state", FileUploadState.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QFileUploadStatus(String variable) {
        super(FileUploadStatus.class, forVariable(variable));
    }

    public QFileUploadStatus(Path<? extends FileUploadStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileUploadStatus(PathMetadata metadata) {
        super(FileUploadStatus.class, metadata);
    }

}

