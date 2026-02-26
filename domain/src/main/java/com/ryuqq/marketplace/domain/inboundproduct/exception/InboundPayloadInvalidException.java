package com.ryuqq.marketplace.domain.inboundproduct.exception;

/**
 * 인바운드 상품 페이로드가 복구 불가능한 상태일 때 발생하는 예외.
 *
 * <p>JSON 구문 오류, 알 수 없는 필드, 타입 불일치, 필수값 누락 등 재시도해도 동일하게 실패하는 경우에 사용합니다. 이 예외가 발생하면
 * PERMANENTLY_FAILED 상태로 전이됩니다.
 */
public class InboundPayloadInvalidException extends InboundProductException {

    private static final InboundProductErrorCode ERROR_CODE =
            InboundProductErrorCode.INBOUND_PRODUCT_PAYLOAD_INVALID;

    public InboundPayloadInvalidException(String message) {
        super(ERROR_CODE, message);
    }

    public InboundPayloadInvalidException(String message, Throwable cause) {
        super(ERROR_CODE, message);
        initCause(cause);
    }
}
