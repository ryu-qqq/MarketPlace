package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 배송 정보.
 *
 * <p>상품주문 상세의 delivery 필드에 매핑.
 *
 * @param deliveryCompany 택배사
 * @param deliveryMethod 배송방법
 * @param deliveryStatus 배송상태
 * @param trackingNumber 운송장번호
 * @param sendDate 발송일시
 * @param deliveredDate 배송완료일시
 * @param pickupDate 수거일시
 * @param isWrongTrackingNumber 잘못된 송장번호 여부
 */
public record NaverDeliveryInfo(
        String deliveryCompany,
        String deliveryMethod,
        String deliveryStatus,
        String trackingNumber,
        String sendDate,
        String deliveredDate,
        String pickupDate,
        Boolean isWrongTrackingNumber) {}
