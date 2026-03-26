package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 클레임 정보.
 *
 * <p>currentClaim 및 completedClaims[]에 공통으로 사용되는 클레임 구조체.
 *
 * @param claimType 클레임 구분 (CANCEL/RETURN/EXCHANGE/PURCHASE_DECISION_HOLDBACK/ADMIN_CANCEL)
 * @param claimId 클레임 번호
 * @param claimStatus 클레임 상태
 * @param claimRequestDate 클레임 요청일 (ISO 8601)
 * @param requestChannel 접수 채널
 * @param claimRequestReason 클레임 요청 사유 코드
 * @param claimRequestDetailContent 클레임 상세 사유
 * @param requestQuantity 요청 수량
 * @param refundExpectedDate 환불 예정일 (ISO 8601)
 * @param refundStandbyReason 환불 대기 사유
 * @param refundStandbyStatus 환불 대기 상태
 * @param holdbackReason 보류 유형
 * @param holdbackDetailedReason 보류 상세 사유
 * @param holdbackStatus 보류 상태 (HOLDBACK/RELEASED)
 * @param collectDeliveryCompany 수거 택배사 코드 (RETURN/EXCHANGE)
 * @param collectTrackingNumber 수거 운송장번호 (RETURN/EXCHANGE)
 * @param collectStatus 수거 상태 (RETURN/EXCHANGE)
 * @param reDeliveryCompany 재배송 택배사 코드 (EXCHANGE)
 * @param reDeliveryTrackingNumber 재배송 운송장번호 (EXCHANGE)
 * @param reDeliveryStatus 재배송 상태 (EXCHANGE)
 */
public record NaverClaimInfo(
        String claimType,
        String claimId,
        String claimStatus,
        String claimRequestDate,
        String requestChannel,
        String claimRequestReason,
        String claimRequestDetailContent,
        Integer requestQuantity,
        String refundExpectedDate,
        String refundStandbyReason,
        String refundStandbyStatus,
        String holdbackReason,
        String holdbackDetailedReason,
        String holdbackStatus,
        String collectDeliveryCompany,
        String collectTrackingNumber,
        String collectStatus,
        String reDeliveryCompany,
        String reDeliveryTrackingNumber,
        String reDeliveryStatus) {}
