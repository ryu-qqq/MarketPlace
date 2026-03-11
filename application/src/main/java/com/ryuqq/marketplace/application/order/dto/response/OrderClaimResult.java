package com.ryuqq.marketplace.application.order.dto.response;

import java.time.Instant;

/**
 * 주문 클레임 조회 결과.
 *
 * @param claimId 클레임 ID
 * @param orderItemId 주문 상품 ID
 * @param claimNumber 클레임번호
 * @param claimType 클레임 유형 (REFUND, EXCHANGE)
 * @param claimStatus 클레임 상태
 * @param quantity 클레임 수량
 * @param reasonType 사유 유형
 * @param reasonDetail 사유 상세
 * @param collectMethod 수거 방법
 * @param originalAmount 원래 금액
 * @param deductionAmount 차감 금액
 * @param deductionReason 차감 사유
 * @param refundAmount 환불 금액
 * @param refundMethod 환불 수단
 * @param refundedAt 환불일시
 * @param requestedAt 요청일시
 * @param completedAt 완료일시
 * @param rejectedAt 거절일시
 */
public record OrderClaimResult(
        long claimId,
        long orderItemId,
        String claimNumber,
        String claimType,
        String claimStatus,
        int quantity,
        String reasonType,
        String reasonDetail,
        String collectMethod,
        int originalAmount,
        int deductionAmount,
        String deductionReason,
        int refundAmount,
        String refundMethod,
        Instant refundedAt,
        Instant requestedAt,
        Instant completedAt,
        Instant rejectedAt) {}
