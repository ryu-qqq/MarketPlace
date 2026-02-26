package com.ryuqq.marketplace.domain.refundpolicy.exception;

/** 기본 환불 정책을 찾을 수 없을 때 예외. */
public class DefaultRefundPolicyNotFoundException extends RefundPolicyException {

    public DefaultRefundPolicyNotFoundException(Long sellerId) {
        super(
                RefundPolicyErrorCode.DEFAULT_REFUND_POLICY_NOT_FOUND,
                String.format("기본 환불 정책이 없습니다. 먼저 기본 환불 정책을 설정해 주세요 (sellerId: %d)", sellerId));
    }
}
