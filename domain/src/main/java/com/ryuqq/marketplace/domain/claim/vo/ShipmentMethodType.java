package com.ryuqq.marketplace.domain.claim.vo;

/** 클레임 배송 방식. */
public enum ShipmentMethodType {
    COURIER, // 택배
    QUICK, // 퀵서비스
    VISIT, // 직접 방문 전달
    AUTO_PICKUP, // 자동 수거
    DESIGNATED_COURIER // 지정 택배
}
