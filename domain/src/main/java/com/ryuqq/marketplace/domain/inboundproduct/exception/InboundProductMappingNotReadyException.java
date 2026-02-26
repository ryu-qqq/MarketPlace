package com.ryuqq.marketplace.domain.inboundproduct.exception;

/** 인바운드 상품 매핑 미완료 예외. */
public class InboundProductMappingNotReadyException extends InboundProductException {

    private static final InboundProductErrorCode ERROR_CODE =
            InboundProductErrorCode.INBOUND_PRODUCT_MAPPING_FAILED;

    public InboundProductMappingNotReadyException() {
        super(ERROR_CODE);
    }

    public InboundProductMappingNotReadyException(Long inboundProductId) {
        super(
                ERROR_CODE,
                String.format("인바운드 상품 매핑이 완료되지 않았습니다. inboundProductId=%d", inboundProductId));
    }
}
