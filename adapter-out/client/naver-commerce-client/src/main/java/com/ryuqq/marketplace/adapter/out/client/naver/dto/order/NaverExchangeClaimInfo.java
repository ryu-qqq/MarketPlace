package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 교환 클레임 정보.
 *
 * <p>상품주문 상세 응답의 최상위 exchange 객체.
 *
 * @param exchangeReason 교환 사유 코드
 * @param exchangeDetailedReason 교환 상세 사유
 * @param holdbackStatus 보류 상태 (HOLDBACK/RELEASED)
 * @param holdbackReason 보류 유형
 * @param requestQuantity 요청 수량
 * @param requestChannel 접수 채널
 * @param claimDeliveryFeeDemandAmount 클레임 배송비 청구 금액
 */
public record NaverExchangeClaimInfo(
        String exchangeReason,
        String exchangeDetailedReason,
        String holdbackStatus,
        String holdbackReason,
        Integer requestQuantity,
        String requestChannel,
        Integer claimDeliveryFeeDemandAmount) {}
