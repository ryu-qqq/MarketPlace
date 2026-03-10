package com.ryuqq.marketplace.application.order.dto.response;

import java.time.Instant;

/**
 * 결제 정보 조회 결과.
 *
 * @param paymentId 결제 ID
 * @param paymentStatus 결제 상태
 * @param paymentMethod 결제 수단
 * @param paymentAgencyId PG사 결제 ID
 * @param paymentAmount 결제 금액
 * @param paidAt 결제일시
 * @param canceledAt 결제취소일시
 */
public record PaymentResult(
        long paymentId,
        String paymentStatus,
        String paymentMethod,
        String paymentAgencyId,
        int paymentAmount,
        Instant paidAt,
        Instant canceledAt) {}
