package com.ryuqq.marketplace.domain.shipment.vo;

/** 배송 상태. */
public enum ShipmentStatus {

    /** 배송 준비 대기 */
    READY,

    /** 배송 준비 중 (발주확인) */
    PREPARING,

    /** 발송 완료 (송장등록) */
    SHIPPED,

    /** 배송 중 */
    IN_TRANSIT,

    /** 배송 완료 */
    DELIVERED,

    /** 배송 실패 */
    FAILED,

    /** 취소 */
    CANCELLED
}
