package com.ryuqq.marketplace.domain.categorypreset.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** CategoryPreset 도메인 에러 코드. */
public enum CategoryPresetErrorCode implements ErrorCode {
    CATEGORY_PRESET_NOT_FOUND("CATPRE-001", 404, "카테고리 프리셋을 찾을 수 없습니다"),
    CATEGORY_PRESET_CHANNEL_MISMATCH("CATPRE-002", 400, "Shop과 카테고리의 판매채널이 일치하지 않습니다"),
    CATEGORY_PRESET_INTERNAL_CATEGORY_NOT_FOUND(
            "CATPRE-003", 400, "요청한 내부 카테고리를 찾을 수 없습니다"),
    CATEGORY_PRESET_SALES_CHANNEL_CATEGORY_NOT_FOUND(
            "CATPRE-004", 404, "판매채널 카테고리를 찾을 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    CategoryPresetErrorCode(String code, int httpStatus, String message) {
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
