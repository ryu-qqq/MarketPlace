package com.ryuqq.marketplace.domain.settlement.entry.vo;

/** 정산 원장 유형. */
public enum EntryType {

    /** 판매 (구매확정). */
    SALES,

    /** 취소 역분개. */
    CANCEL,

    /** 환불 역분개. */
    REFUND,

    /** 교환 출고 (원래 상품 반환). */
    EXCHANGE_OUT,

    /** 교환 입고 (새 상품 출고). */
    EXCHANGE_IN,

    /** 수동 조정. */
    ADJUSTMENT;

    /** 역분개 유형인지 확인합니다. */
    public boolean isReversal() {
        return this == CANCEL || this == REFUND || this == EXCHANGE_OUT;
    }
}
