package com.ryuqq.marketplace.domain.inboundsource.exception;

/** 인바운드 소스 코드가 이미 존재하는 경우 예외. */
public class InboundSourceDuplicateException extends InboundSourceException {

    private static final InboundSourceErrorCode ERROR_CODE =
            InboundSourceErrorCode.INBOUND_SOURCE_CODE_DUPLICATE;

    public InboundSourceDuplicateException(String code) {
        super(ERROR_CODE, String.format("인바운드 소스 코드 '%s'가 이미 존재합니다", code));
    }
}
