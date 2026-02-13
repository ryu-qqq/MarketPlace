package com.ryuqq.marketplace.application.shipment.dto.response;

/**
 * 배송 상태별 요약 결과.
 *
 * @param ready 배송 준비 대기
 * @param preparing 배송 준비 중
 * @param shipped 발송 완료
 * @param inTransit 배송 중
 * @param delivered 배송 완료
 * @param failed 배송 실패
 * @param cancelled 취소
 */
public record ShipmentSummaryResult(
        int ready,
        int preparing,
        int shipped,
        int inTransit,
        int delivered,
        int failed,
        int cancelled) {}
