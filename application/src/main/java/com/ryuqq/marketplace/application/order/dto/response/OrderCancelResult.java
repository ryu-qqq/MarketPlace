package com.ryuqq.marketplace.application.order.dto.response;

import java.time.Instant;

/**
 * 주문 취소 조회 결과.
 *
 * @param cancelId 취소 ID
 * @param orderItemId 주문 상품 ID
 * @param cancelNumber 취소번호
 * @param cancelStatus 취소 상태
 * @param quantity 취소 수량
 * @param reasonType 취소 사유 유형
 * @param reasonDetail 취소 사유 상세
 * @param originalAmount 원래 금액
 * @param refundAmount 환불 금액
 * @param refundMethod 환불 수단
 * @param refundedAt 환불일시
 * @param requestedAt 요청일시
 * @param completedAt 완료일시
 */
public record OrderCancelResult(
        long cancelId,
        long orderItemId,
        String cancelNumber,
        String cancelStatus,
        int quantity,
        String reasonType,
        String reasonDetail,
        int originalAmount,
        int refundAmount,
        String refundMethod,
        Instant refundedAt,
        Instant requestedAt,
        Instant completedAt) {}
