package com.ryuqq.marketplace.domain.refund.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;
import java.time.Instant;

/** 환불 금액 정보 Value Object. */
public record RefundInfo(
        Money originalAmount,
        Money finalAmount,
        Money deductionAmount,
        String deductionReason,
        String refundMethod,
        Instant refundedAt) {

    public RefundInfo {
        if (originalAmount == null) {
            throw new IllegalArgumentException("원래 금액은 null일 수 없습니다");
        }
        if (finalAmount == null) {
            throw new IllegalArgumentException("최종 환불 금액은 null일 수 없습니다");
        }
        if (deductionAmount == null) {
            throw new IllegalArgumentException("차감 금액은 null일 수 없습니다");
        }
    }

    public static RefundInfo of(
            Money originalAmount,
            Money finalAmount,
            Money deductionAmount,
            String deductionReason,
            String refundMethod,
            Instant refundedAt) {
        return new RefundInfo(
                originalAmount,
                finalAmount,
                deductionAmount,
                deductionReason,
                refundMethod,
                refundedAt);
    }

    public static RefundInfo fullRefund(Money amount, String refundMethod, Instant refundedAt) {
        return new RefundInfo(amount, amount, Money.zero(), null, refundMethod, refundedAt);
    }

    public static RefundInfo partialRefund(
            Money originalAmount,
            Money deductionAmount,
            String deductionReason,
            String refundMethod,
            Instant refundedAt) {
        return new RefundInfo(
                originalAmount,
                originalAmount.subtract(deductionAmount),
                deductionAmount,
                deductionReason,
                refundMethod,
                refundedAt);
    }
}
