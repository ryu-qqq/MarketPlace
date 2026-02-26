package com.ryuqq.marketplace.domain.commoncodetype.exception;

/**
 * 활성화된 공통 코드가 존재하여 비활성화할 수 없는 경우 예외.
 *
 * <p>공통 코드 타입을 비활성화하려 할 때, 해당 타입에 활성화된 공통 코드가 존재하면 발생합니다.
 */
public class ActiveCommonCodesExistException extends CommonCodeTypeException {

    private static final CommonCodeTypeErrorCode ERROR_CODE =
            CommonCodeTypeErrorCode.ACTIVE_COMMON_CODES_EXIST;

    public ActiveCommonCodesExistException() {
        super(ERROR_CODE);
    }

    public ActiveCommonCodesExistException(String typeCode) {
        super(
                ERROR_CODE,
                String.format("공통 코드 타입 '%s'에 활성화된 공통 코드가 존재합니다. 먼저 해당 공통 코드를 비활성화하세요", typeCode));
    }
}
