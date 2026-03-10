package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 주문 레벨 정보.
 *
 * <p>상품주문 상세의 order 필드에 매핑.
 *
 * @param orderId 주문번호
 * @param orderDate 주문일시 (ISO 8601)
 * @param paymentDate 결제일시 (ISO 8601)
 * @param ordererName 주문자명
 * @param ordererTel 주문자 연락처
 * @param ordererId 주문자 네이버 ID (마스킹)
 * @param ordererNo 주문자 번호
 * @param payLocationType 결제 위치 유형 (PC/MOBILE)
 * @param paymentMeans 결제수단 (신용카드/간편결제/휴대폰/계좌 등)
 * @param chargeAmountPaymentAmount 충전금 최종 결제 금액
 * @param checkoutAccumulationPaymentAmount 네이버페이 적립금 최종 결제 금액
 * @param generalPaymentAmount 일반 결제 수단 최종 결제 금액
 * @param naverMileagePaymentAmount 네이버페이 포인트 최종 결제 금액
 * @param orderDiscountAmount 주문 할인액
 * @param paymentDueDate 결제 기한 (ISO 8601)
 * @param isDeliveryMemoParticularInput 배송 메모 개별 입력 여부
 * @param payLaterPaymentAmount 후불 결제 최종 결제 금액
 */
public record NaverProductOrderOrder(
        String orderId,
        String orderDate,
        String paymentDate,
        String ordererName,
        String ordererTel,
        String ordererId,
        String ordererNo,
        String payLocationType,
        String paymentMeans,
        Integer chargeAmountPaymentAmount,
        Integer checkoutAccumulationPaymentAmount,
        Integer generalPaymentAmount,
        Integer naverMileagePaymentAmount,
        Integer orderDiscountAmount,
        String paymentDueDate,
        String isDeliveryMemoParticularInput,
        Integer payLaterPaymentAmount) {}
