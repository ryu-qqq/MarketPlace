package com.ryuqq.marketplace.application.claimsync.dto.external;

import java.time.Instant;

/**
 * 외부몰 클레임 데이터.
 *
 * @param externalOrderId 외부 주문번호
 * @param externalProductOrderId 외부 주문상품번호
 * @param claimType 클레임 유형 (CANCEL, RETURN, EXCHANGE, ADMIN_CANCEL)
 * @param claimStatus 클레임 상태 (CANCEL_REQUEST, COLLECTING 등)
 * @param claimId 외부 채널 클레임 식별자
 * @param productOrderStatus 주문상품 상태 (CANCELED, RETURNED 등)
 * @param claimReason 클레임 사유 (내부 정규화 코드 또는 자유 텍스트)
 * @param claimDetailedReason 클레임 상세 사유
 * @param externalReasonCode 외부 채널 원본 사유 코드 (예: 네이버 INTENT_CHANGED, BROKEN 등). null 허용.
 * @param requestQuantity 요청 수량
 * @param requestChannel 요청 채널 (BUYER, SELLER 등)
 * @param collectDeliveryCompany 수거 택배사
 * @param collectTrackingNumber 수거 송장번호
 * @param collectStatus 수거 상태
 * @param reDeliveryCompany 재배송 택배사 (교환)
 * @param reDeliveryTrackingNumber 재배송 송장번호 (교환)
 * @param reDeliveryStatus 재배송 상태 (교환)
 * @param holdbackStatus 보류 상태 (HOLDBACK/RELEASED). null 허용.
 * @param holdbackReason 보류 유형. null 허용.
 * @param claimRequestDate 클레임 요청일시
 * @param lastChangedDate 마지막 변경일시
 */
public record ExternalClaimPayload(
        String externalOrderId,
        String externalProductOrderId,
        String claimType,
        String claimStatus,
        String claimId,
        String productOrderStatus,
        String claimReason,
        String claimDetailedReason,
        String externalReasonCode,
        Integer requestQuantity,
        String requestChannel,
        String collectDeliveryCompany,
        String collectTrackingNumber,
        String collectStatus,
        String reDeliveryCompany,
        String reDeliveryTrackingNumber,
        String reDeliveryStatus,
        String holdbackStatus,
        String holdbackReason,
        Instant claimRequestDate,
        Instant lastChangedDate) {}
