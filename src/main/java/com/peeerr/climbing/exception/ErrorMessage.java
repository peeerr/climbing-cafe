package com.peeerr.climbing.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorMessage {

    /* CategoryService */
    ALREADY_EXISTS_CATEGORY("이미 존재하는 카테고리입니다.", CONFLICT),

    /* FileService */
    ALREADY_DELETED_FILE("이미 삭제된 파일입니다.", BAD_REQUEST),

    /* StorageManagement */
    FILE_STORE_FAILED("파일 저장에 실패했습니다.", INTERNAL_SERVER_ERROR),

    /* S3FileUploader */
    INVALID_FILE_TYPE("이미지 파일 형식만 첨부가 가능합니다. (JPEG, PNG, GIF)", UNSUPPORTED_MEDIA_TYPE),

    /* MemberService */
    LOGIN_FAILED("아이디 또는 비밀번호가 올바르지 않습니다.", UNAUTHORIZED),
    ALREADY_EXISTS_USERNAME("이미 존재하는 닉네임입니다.", CONFLICT),
    ALREADY_EXISTS_EMAIL("이미 존재하는 이메일입니다.", CONFLICT),

    /* LikeService */
    ALREADY_EXISTS_LIKE("이미 좋아요를 누른 게시물입니다.", CONFLICT),
    LIKE_NOT_FOUND("해당 게시물에 좋아요를 누르지 않았습니다.", NOT_FOUND),

    /* Post */
    POST_NOT_FOUND("존재하지 않는 게시물입니다.", NOT_FOUND),

    /* Category */
    CATEGORY_NOT_FOUND("존재하지 않는 카테고리입니다.", NOT_FOUND),

    /* Member */
    MEMBER_NOT_FOUND("존재하지 않는 아이디입니다.", NOT_FOUND),
    ACCESS_DENIED("접근 권한이 없습니다.", FORBIDDEN),
    LOGIN_REQUIRED("로그인 후 이용해 주세요.", UNAUTHORIZED),

    /* File */
    FILE_NOT_FOUND("존재하지 않는 파일입니다.", NOT_FOUND),
    FILE_SIZE_EXCEEDED("전체 파일 크기를 10MB 이하로 첨부해 주세요.", PAYLOAD_TOO_LARGE),

    /* Comment */
    COMMENT_NOT_FOUND("존재하지 않는 댓글입니다.", NOT_FOUND),

    /* FileController */
    FILE_REQUIRED("파일을 선택하지 않았습니다. 파일을 선택해주세요.", BAD_REQUEST);

    private final String message;
    private final HttpStatus status;

    ErrorMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

}
