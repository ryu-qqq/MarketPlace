package com.ryuqq.marketplace.domain.category.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/**
 * Category Error Code Enum
 *
 * <p><strong>카테고리 도메인 에러 코드</strong>:</p>
 * <ul>
 *   <li>CATEGORY-001 - 카테고리를 찾을 수 없음</li>
 *   <li>CATEGORY-002 - 카테고리 코드 중복</li>
 *   <li>CATEGORY-003 - 하위 카테고리 존재</li>
 *   <li>CATEGORY-004 - 최대 깊이 초과</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum CategoryErrorCode implements ErrorCode {
    CATEGORY_NOT_FOUND("CATEGORY-001", 404, "카테고리를 찾을 수 없습니다"),
    CATEGORY_CODE_DUPLICATE("CATEGORY-002", 409, "카테고리 코드가 중복됩니다"),
    CATEGORY_HAS_CHILDREN("CATEGORY-003", 400, "하위 카테고리가 있어 삭제할 수 없습니다"),
    CATEGORY_MAX_DEPTH_EXCEEDED("CATEGORY-004", 400, "카테고리 최대 깊이를 초과했습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    CategoryErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
