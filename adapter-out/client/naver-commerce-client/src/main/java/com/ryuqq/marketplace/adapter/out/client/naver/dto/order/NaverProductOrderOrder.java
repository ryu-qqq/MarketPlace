package com.ryuqq.marketplace.adapter.out.client.naver.dto.order;

/**
 * 네이버 커머스 주문 레벨 정보.
 *
 * <p>상품주문 상세의 order 필드에 매핑.
 *
 * @param orderId 주문번호
 * @param orderDate 주문일시 (ISO 8601)
 * @param paymentDate 결제일시 (ISO 8601)
 * @param payLocationType 결제수단
 * @param ordererName 주문자명
 * @param ordererTel 주문자 연락처
 * @param ordererEmail 주문자 이메일
 */
public record NaverProductOrderOrder(
        String orderId,
        String orderDate,
        String paymentDate,
        String payLocationType,
        String ordererName,
        String ordererTel,
        String ordererEmail) {}
