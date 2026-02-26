package com.ryuqq.marketplace.domain.inboundsource.exception;

/** 인바운드 소스를 찾을 수 없는 경우 예외. */
public class InboundSourceNotFoundException extends InboundSourceException {

    private static final InboundSourceErrorCode ERROR_CODE =
            InboundSourceErrorCode.INBOUND_SOURCE_NOT_FOUND;

    public InboundSourceNotFoundException() {
        super(ERROR_CODE);
    }

    public InboundSourceNotFoundException(Long inboundSourceId) {
        super(ERROR_CODE, String.format("ID가 %d인 인바운드 소스를 찾을 수 없습니다", inboundSourceId));
    }

    public InboundSourceNotFoundException(String code) {
        super(ERROR_CODE, String.format("코드가 %s인 인바운드 소스를 찾을 수 없습니다", code));
    }
}
