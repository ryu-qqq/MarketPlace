package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 배송 정보.
 *
 * <p>상품주문 상세의 delivery 필드에 매핑.
 *
 * @param deliveryCompany 택배사 코드
 * @param deliveryMethod 배송방법 코드
 * @param deliveryStatus 배송상태
 * @param trackingNumber 운송장번호
 * @param sendDate 발송일시 (ISO 8601)
 * @param deliveredDate 배송완료일시 (ISO 8601)
 * @param pickupDate 집화일시 (ISO 8601)
 * @param isWrongTrackingNumber 잘못된 송장번호 여부
 * @param wrongTrackingNumberRegisteredDate 오류 송장 등록 일시 (ISO 8601)
 * @param wrongTrackingNumberType 오류 사유
 */
public record NaverDeliveryInfo(
        String deliveryCompany,
        String deliveryMethod,
        String deliveryStatus,
        String trackingNumber,
        String sendDate,
        String deliveredDate,
        String pickupDate,
        Boolean isWrongTrackingNumber,
        String wrongTrackingNumberRegisteredDate,
        String wrongTrackingNumberType) {}
