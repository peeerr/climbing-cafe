package com.peeerr.climbing.exception.constant;

public class ErrorMessage {

    /* Validation */
    public static final String VALIDATION_ERROR = "입력 데이터의 유효성을 검사하던 중 문제가 발생했습니다.";
    public static final String NO_FILE_SELECTED = "파일을 선택하지 않았습니다. 파일을 선택해주세요.";

    /* FileService */
    public static final String FILE_ALREADY_DELETED = "이미 삭제된 파일입니다.";

    /* StorageManagement */
    public static final String FILE_STORE_FAILED = "파일 저장에 실패했습니다.";
    public static final String DIRECTORY_CREATE_FAILED = "파일을 저장할 디렉토리 생성에 실패했습니다.";

    /* UserService */
    public static final String PASSWORD_CONFIRMATION_FAILED = "비밀번호 확인에 실패했습니다. 동일한 비밀번호를 입력해 주세요.";

    /* Post */
    public static final String POST_NOT_FOUND = "존재하지 않는 게시물입니다.";

    /* Category */
    public static final String CATEGORY_NOT_FOUND = "존재하지 않는 카테고리입니다.";

    /* User */
    public static final String USER_NOT_FOUND = "존재하지 않는 아이디입니다.";

    /* File */
    public static final String FILE_NOT_FOUND = "존재하지 않는 파일입니다.";
    public static final String FILE_SIZE_EXCEEDED = "전체 파일 크기를 10MB 이하로 첨부해 주세요.";

}
