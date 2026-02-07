package com.ryuqq.marketplace.domain.commoncodetype.exception;

/**
 * 공통 코드 타입 표시 순서가 중복되는 경우 예외.
 *
 * <p>동일한 표시 순서가 이미 존재할 때 발생합니다.
 */
public class DuplicateCommonCodeTypeDisplayOrderException extends CommonCodeTypeException {

    private static final CommonCodeTypeErrorCode ERROR_CODE =
            CommonCodeTypeErrorCode.DUPLICATE_DISPLAY_ORDER;

    public DuplicateCommonCodeTypeDisplayOrderException() {
        super(ERROR_CODE);
    }

    public DuplicateCommonCodeTypeDisplayOrderException(int displayOrder) {
        super(ERROR_CODE, String.format("표시 순서 '%d'는 이미 존재합니다", displayOrder));
    }
}
