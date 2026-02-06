package com.ryuqq.marketplace.domain.commoncodetype.exception;

/**
 * 공통 코드 타입 코드가 중복되는 경우 예외.
 *
 * <p>동일한 코드가 이미 존재할 때 발생합니다.
 */
public class DuplicateCommonCodeTypeCodeException extends CommonCodeTypeException {

    private static final CommonCodeTypeErrorCode ERROR_CODE =
            CommonCodeTypeErrorCode.DUPLICATE_CODE;

    public DuplicateCommonCodeTypeCodeException() {
        super(ERROR_CODE);
    }

    public DuplicateCommonCodeTypeCodeException(String code) {
        super(ERROR_CODE, String.format("코드 '%s'는 이미 존재합니다", code));
    }
}
