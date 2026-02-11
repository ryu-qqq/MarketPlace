package com.ryuqq.marketplace.domain.channeloptionmapping.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/** ChannelOptionMapping 도메인 에러 코드. */
public enum ChannelOptionMappingErrorCode implements ErrorCode {
    CHANNEL_OPTION_MAPPING_NOT_FOUND("CHOPT-001", 404, "채널 옵션 매핑을 찾을 수 없습니다"),
    CHANNEL_OPTION_MAPPING_DUPLICATE("CHOPT-002", 409, "이미 존재하는 채널 옵션 매핑입니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    ChannelOptionMappingErrorCode(String code, int httpStatus, String message) {
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
