package com.ryuqq.marketplace.application.order.dto.response;

/**
 * 주문 상태별 요약 결과.
 *
 * @param ordered 주문접수
 * @param preparing 발주확인
 * @param shipped 발송
 * @param delivered 배송완료
 * @param confirmed 구매확정
 * @param cancelled 취소
 * @param claimInProgress 클레임 진행중
 * @param refunded 환불완료
 * @param exchanged 교환완료
 */
public record OrderSummaryResult(
        int ordered,
        int preparing,
        int shipped,
        int delivered,
        int confirmed,
        int cancelled,
        int claimInProgress,
        int refunded,
        int exchanged) {}
