package com.ryuqq.marketplace.domain.inboundproduct.exception;

/** 인바운드 상품 조회 실패 예외. */
public class InboundProductNotFoundException extends InboundProductException {

    private static final InboundProductErrorCode ERROR_CODE =
            InboundProductErrorCode.INBOUND_PRODUCT_NOT_FOUND;

    public InboundProductNotFoundException(Long inboundSourceId, String externalProductCode) {
        super(
                ERROR_CODE,
                String.format(
                        "인바운드 상품을 찾을 수 없습니다. inboundSourceId=%d, externalProductCode=%s",
                        inboundSourceId, externalProductCode));
    }
}
