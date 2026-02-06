package com.ryuqq.marketplace.domain.commoncode.exception;

/**
 * 공통 코드가 중복되는 경우 예외.
 *
 * <p>동일한 타입과 코드 조합이 이미 존재할 때 발생합니다.
 */
public class CommonCodeDuplicateException extends CommonCodeException {

    private static final CommonCodeErrorCode ERROR_CODE = CommonCodeErrorCode.COMMON_CODE_DUPLICATE;

    public CommonCodeDuplicateException() {
        super(ERROR_CODE);
    }

    public CommonCodeDuplicateException(String type, String code) {
        super(ERROR_CODE, String.format("타입 '%s', 코드 '%s'는 이미 존재합니다", type, code));
    }
}
