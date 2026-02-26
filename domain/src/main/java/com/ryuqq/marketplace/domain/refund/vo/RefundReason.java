package com.ryuqq.marketplace.domain.refund.vo;

/** 환불 사유 Value Object. */
public record RefundReason(RefundReasonType reasonType, String reasonDetail) {

    public RefundReason {
        if (reasonType == null) {
            throw new IllegalArgumentException("환불 사유 유형은 null일 수 없습니다");
        }
    }

    public static RefundReason of(RefundReasonType reasonType, String reasonDetail) {
        return new RefundReason(reasonType, reasonDetail);
    }
}
