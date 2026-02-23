package com.ryuqq.marketplace.domain.productintelligence.exception;

/**
 * 아웃박스 상태 전환이 유효하지 않은 경우 예외.
 *
 * <p>현재 아웃박스 상태에서 요청된 액션을 수행할 수 없을 때 발생합니다.
 */
public class InvalidOutboxStateException extends ProductIntelligenceException {

    private static final ProductIntelligenceErrorCode ERROR_CODE =
            ProductIntelligenceErrorCode.INVALID_OUTBOX_STATE;

    public InvalidOutboxStateException(String currentStatus, String action) {
        super(ERROR_CODE, String.format("%s은 현재 상태(%s)에서 불가능합니다", action, currentStatus));
    }
}
