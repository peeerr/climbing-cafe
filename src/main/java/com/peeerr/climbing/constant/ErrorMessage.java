package com.peeerr.climbing.constant;

public class ErrorMessage {

    /* Validation */
    public static final String VALIDATION_ERROR = "입력 데이터의 유효성을 검사하던 중 문제가 발생했습니다.";
    public static final String NO_FILE_SELECTED = "파일을 선택하지 않았습니다. 파일을 선택해주세요.";

    /* CategoryService */
    public static final String CATEGORY_DUPLICATED = "이미 존재하는 카테고리입니다.";

    /* FileService */
    public static final String FILE_ALREADY_DELETED = "이미 삭제된 파일입니다.";

    /* StorageManagement */
    public static final String FILE_STORE_FAILED = "파일 저장에 실패했습니다.";

    /* S3FileUploader */
    public static final String INVALID_FILE_TYPE = "이미지 파일 형식만 첨부가 가능합니다. (JPEG, PNG, GIF)";

    /* MemberService */
    public static final String PASSWORD_CONFIRMATION_FAILED = "비밀번호 확인에 실패했습니다. 동일한 비밀번호를 입력해 주세요.";
    public static final String INVALID_PASSWORD = "비밀번호가 일치하지 않습니다.";
    public static final String USERNAME_DUPLICATED = "이미 존재하는 닉네임입니다.";
    public static final String EMAIL_DUPLICATED = "이미 존재하는 이메일입니다.";

    /* LikeService */
    public static final String LIKE_ALREADY_EXISTS = "이미 좋아요를 누른 게시물입니다.";
    public static final String LIKE_NOT_FOUND = "해당 게시물에 좋아요를 누르지 않았습니다.";

    /* Post */
    public static final String POST_NOT_FOUND = "존재하지 않는 게시물입니다.";

    /* Category */
    public static final String CATEGORY_NOT_FOUND = "존재하지 않는 카테고리입니다.";

    /* Member */
    public static final String MEMBER_NOT_FOUND = "존재하지 않는 아이디입니다.";
    public static final String NO_ACCESS_PERMISSION = "접근 권한이 없습니다.";
    public static final String LOGIN_REQUIRED = "로그인 후 이용해 주세요.";

    /* File */
    public static final String FILE_NOT_FOUND = "존재하지 않는 파일입니다.";
    public static final String FILE_SIZE_EXCEEDED = "전체 파일 크기를 10MB 이하로 첨부해 주세요.";

    /* Comment */
    public static final String COMMENT_NOT_FOUND = "존재하지 않는 댓글입니다.";

}
