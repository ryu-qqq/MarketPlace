package com.ryuqq.marketplace.domain.cancel.outbox.vo;

/**
 * 취소 아웃박스 유형.
 *
 * <p>외부 채널에 동기화해야 하는 취소 상태 변경 유형입니다.
 */
public enum CancelOutboxType {

    /** 판매자 취소 (즉시 APPROVED) */
    SELLER_CANCEL("판매자 취소"),

    /** 구매자 취소 승인 (REQUESTED → APPROVED) */
    APPROVE("취소 승인"),

    /** 구매자 취소 거절 (REQUESTED → REJECTED) */
    REJECT("취소 거절");

    private final String description;

    CancelOutboxType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
