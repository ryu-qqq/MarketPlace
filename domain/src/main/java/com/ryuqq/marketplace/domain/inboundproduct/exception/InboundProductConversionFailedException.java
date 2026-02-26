package com.ryuqq.marketplace.domain.inboundproduct.exception;

/** 인바운드 상품 변환 실패 예외. */
public class InboundProductConversionFailedException extends InboundProductException {

    private static final InboundProductErrorCode ERROR_CODE =
            InboundProductErrorCode.INBOUND_PRODUCT_CONVERSION_FAILED;

    public InboundProductConversionFailedException() {
        super(ERROR_CODE);
    }

    public InboundProductConversionFailedException(Long inboundProductId) {
        super(
                ERROR_CODE,
                String.format("인바운드 상품 변환에 실패했습니다. inboundProductId=%d", inboundProductId));
    }
}
