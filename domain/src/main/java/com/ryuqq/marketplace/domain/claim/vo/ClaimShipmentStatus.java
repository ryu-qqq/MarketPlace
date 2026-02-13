package com.ryuqq.marketplace.domain.claim.vo;

/** 클레임 배송(수거) 상태. */
public enum ClaimShipmentStatus {
    PENDING, // 수거 대기
    IN_TRANSIT, // 수거 중
    DELIVERED, // 수거 완료
    FAILED // 수거 실패
}
