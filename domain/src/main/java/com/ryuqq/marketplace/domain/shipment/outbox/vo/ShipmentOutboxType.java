package com.ryuqq.marketplace.domain.shipment.outbox.vo;

/**
 * 배송 아웃박스 유형.
 *
 * <p>외부 채널에 동기화해야 하는 배송 상태 변경 유형입니다.
 */
public enum ShipmentOutboxType {

    /** 발주확인 (READY → PREPARING) */
    CONFIRM("발주확인"),

    /** 송장등록/발송처리 (PREPARING → SHIPPED) */
    SHIP("발송처리"),

    /** 배송완료 (IN_TRANSIT → DELIVERED) */
    DELIVER("배송완료"),

    /** 취소 (→ CANCELLED) */
    CANCEL("취소");

    private final String description;

    ShipmentOutboxType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
