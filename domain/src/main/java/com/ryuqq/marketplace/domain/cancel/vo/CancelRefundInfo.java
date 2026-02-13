package com.ryuqq.marketplace.domain.cancel.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;
import java.time.Instant;

/** 취소 환불 정보 Value Object. */
public record CancelRefundInfo(
        Money refundAmount,
        String refundMethod,
        String refundStatus,
        Instant refundedAt,
        String pgRefundId) {

    public CancelRefundInfo {
        if (refundAmount == null) {
            throw new IllegalArgumentException("환불 금액은 필수입니다");
        }
    }

    public static CancelRefundInfo of(
            Money refundAmount,
            String refundMethod,
            String refundStatus,
            Instant refundedAt,
            String pgRefundId) {
        return new CancelRefundInfo(
                refundAmount, refundMethod, refundStatus, refundedAt, pgRefundId);
    }
}
