package com.ryuqq.marketplace.domain.commoncodetype.exception;

/**
 * 공통 코드 타입을 찾을 수 없는 경우 예외.
 *
 * <p>요청한 ID에 해당하는 공통 코드 타입이 존재하지 않을 때 발생합니다.
 */
public class CommonCodeTypeNotFoundException extends CommonCodeTypeException {

    private static final CommonCodeTypeErrorCode ERROR_CODE = CommonCodeTypeErrorCode.NOT_FOUND;

    public CommonCodeTypeNotFoundException() {
        super(ERROR_CODE);
    }

    public CommonCodeTypeNotFoundException(Long id) {
        super(ERROR_CODE, String.format("ID가 %d인 공통 코드 타입을 찾을 수 없습니다", id));
    }
}
