package com.ryuqq.marketplace.domain.commoncode.exception;

/**
 * 공통 코드를 찾을 수 없는 경우 예외.
 *
 * <p>요청한 ID에 해당하는 공통 코드가 존재하지 않을 때 발생합니다.
 */
public class CommonCodeNotFoundException extends CommonCodeException {

    private static final CommonCodeErrorCode ERROR_CODE = CommonCodeErrorCode.COMMON_CODE_NOT_FOUND;

    public CommonCodeNotFoundException() {
        super(ERROR_CODE);
    }

    public CommonCodeNotFoundException(Long id) {
        super(ERROR_CODE, String.format("ID가 %d인 공통 코드를 찾을 수 없습니다", id));
    }
}
