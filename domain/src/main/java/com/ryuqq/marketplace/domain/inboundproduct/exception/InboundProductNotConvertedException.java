package com.ryuqq.marketplace.domain.inboundproduct.exception;

/** 인바운드 상품이 아직 내부 상품으로 변환되지 않은 경우 예외. */
public class InboundProductNotConvertedException extends InboundProductException {

    private static final InboundProductErrorCode ERROR_CODE =
            InboundProductErrorCode.INBOUND_PRODUCT_CONVERSION_FAILED;

    public InboundProductNotConvertedException(long setofProductGroupId) {
        super(
                ERROR_CODE,
                String.format(
                        "인바운드 상품이 아직 내부 상품으로 변환되지 않았습니다. setofProductGroupId=%d",
                        setofProductGroupId));
    }
}
