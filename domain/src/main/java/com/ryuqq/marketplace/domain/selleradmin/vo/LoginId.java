package com.ryuqq.marketplace.domain.selleradmin.vo;

import java.util.regex.Pattern;

/**
 * 로그인 ID Value Object.
 *
 * <p>셀러 관리자의 로그인 ID입니다. 이메일 형식 또는 일반 아이디 형식을 지원합니다.
 */
public record LoginId(String value) {

    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 100;
    private static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+@-]+$");

    public LoginId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("로그인 ID는 필수입니다");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "로그인 ID는 " + MIN_LENGTH + "자 이상 " + MAX_LENGTH + "자 이하여야 합니다");
        }
        if (!LOGIN_ID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("유효하지 않은 로그인 ID 형식입니다: " + value);
        }
    }

    public static LoginId of(String value) {
        return new LoginId(value);
    }

    /** 이메일 형식인지 확인합니다. */
    public boolean isEmailFormat() {
        return value.contains("@");
    }
}
