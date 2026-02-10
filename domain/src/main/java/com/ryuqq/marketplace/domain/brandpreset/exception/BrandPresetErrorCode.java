package com.ryuqq.marketplace.domain.brandpreset.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** BrandPreset 도메인 에러 코드. */
public enum BrandPresetErrorCode implements ErrorCode {
    BRAND_PRESET_NOT_FOUND("BRDPRE-001", 404, "브랜드 프리셋을 찾을 수 없습니다"),
    BRAND_PRESET_CHANNEL_MISMATCH("BRDPRE-002", 400, "Shop과 브랜드의 판매채널이 일치하지 않습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    BrandPresetErrorCode(String code, int httpStatus, String message) {
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
