package com.ryuqq.marketplace.domain.externalsource.exception;

/** 외부 소스를 찾을 수 없는 경우 예외. */
public class ExternalSourceNotFoundException extends ExternalSourceException {

    private static final ExternalSourceErrorCode ERROR_CODE =
            ExternalSourceErrorCode.EXTERNAL_SOURCE_NOT_FOUND;

    public ExternalSourceNotFoundException() {
        super(ERROR_CODE);
    }

    public ExternalSourceNotFoundException(Long externalSourceId) {
        super(ERROR_CODE, String.format("ID가 %d인 외부 소스를 찾을 수 없습니다", externalSourceId));
    }

    public ExternalSourceNotFoundException(String code) {
        super(ERROR_CODE, String.format("코드가 %s인 외부 소스를 찾을 수 없습니다", code));
    }
}
