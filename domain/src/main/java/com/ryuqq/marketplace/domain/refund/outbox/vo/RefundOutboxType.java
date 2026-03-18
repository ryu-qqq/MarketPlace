package com.ryuqq.marketplace.domain.refund.outbox.vo;

/**
 * 환불 아웃박스 유형.
 *
 * <p>외부 채널에 동기화해야 하는 환불 상태 변경 유형입니다.
 */
public enum RefundOutboxType {

    /** 환불 요청 (REQUESTED) */
    REQUEST("환불 요청"),

    /** 환불 승인 (수거 시작, COLLECTING) */
    APPROVE("환불 승인"),

    /** 환불 거절 (REJECTED) */
    REJECT("환불 거절"),

    /** 환불 완료 (COMPLETED) */
    COMPLETE("환불 완료");

    private final String description;

    RefundOutboxType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
