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
 * @param payLocationType 결제 위치 유형
 * @param paymentMeans 결제수단
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
        String paymentMeans) {}
