package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 반품 클레임 정보.
 *
 * <p>상품주문 상세 응답의 최상위 return 객체.
 *
 * @param returnReason 반품 사유 코드
 * @param returnDetailedReason 반품 상세 사유
 * @param holdbackStatus 보류 상태 (HOLDBACK/RELEASED)
 * @param holdbackReason 보류 유형
 * @param requestQuantity 요청 수량
 * @param requestChannel 접수 채널
 * @param collectDeliveryCompany 수거 택배사 코드
 * @param collectTrackingNumber 수거 운송장번호
 * @param collectStatus 수거 상태
 */
public record NaverReturnClaimInfo(
        String returnReason,
        String returnDetailedReason,
        String holdbackStatus,
        String holdbackReason,
        Integer requestQuantity,
        String requestChannel,
        String collectDeliveryCompany,
        String collectTrackingNumber,
        String collectStatus) {}
