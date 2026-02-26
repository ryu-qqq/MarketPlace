package com.ryuqq.marketplace.domain.commoncode.exception;

/**
 * 비활성화된 공통 코드에 대한 작업 시 예외.
 *
 * <p>비활성화된 공통 코드에 대해 허용되지 않는 작업을 시도할 때 발생합니다.
 */
public class CommonCodeInactiveException extends CommonCodeException {

    private static final CommonCodeErrorCode ERROR_CODE = CommonCodeErrorCode.COMMON_CODE_INACTIVE;

    public CommonCodeInactiveException() {
        super(ERROR_CODE);
    }
}
