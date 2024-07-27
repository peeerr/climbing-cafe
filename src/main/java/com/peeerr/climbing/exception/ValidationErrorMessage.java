package com.peeerr.climbing.exception;

public class ValidationErrorMessage {

    public static final String VALIDATION_ERROR = "입력 데이터의 유효성을 검사하던 중 문제가 발생했습니다.";

    public static final String INVALID_USERNAME_LENGTH = "2자 이상 16자 이하, 영어 또는 숫자 또는 한글로 구성해 주세요.";
    public static final String INVALID_PASSWORD_LENGTH = "비밀번호는 8자 이상 20자 이하로 입력해 주세요.";
    public static final String INVALID_PASSWORD_PATTERN = "비밀번호는 영문자와 숫자를 최소 1개 이상 포함하여 입력해 주세요.";
    public static final String INVALID_EMAIL_PATTERN = "이메일 주소 양식을 확인해 주세요";
    public static final String USERNAME_NOT_BLANK = "닉네임을 입력해 주세요.";
    public static final String EMAIL_NOT_BLANK = "이메일을 입력해 주세요.";
    public static final String PASSWORD_NOT_BLANK = "비밀번호를 입력해 주세요.";
    public static final String CHECK_PASSWORD_NOT_BLANK = "비밀번호를 다시 입력해 주세요.";

    public static final String PASSWORD_MISMATCH = "비밀번호 확인에 실패했습니다. 동일한 비밀번호를 입력해 주세요.";
    public static final String CHECK_PASSWORD = "checkPassword";

    public static final String CATEGORY_NAME_NOT_BLANK = "카테고리명을 입력해 주세요.";

    public static final String CONTENT_NOT_BLANK = "내용을 입력해 주세요.";

    public static final String CATEGORY_NOT_NULL = "카테고리를 선택해 주세요.";
    public static final String TITLE_NOT_BLANK = "제목을 입력해 주세요.";

    public static final String FILE_REQUIRED = "파일을 선택하지 않았습니다. 파일을 선택해주세요.";

}
