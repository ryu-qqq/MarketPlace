package com.ryuqq.marketplace.domain.refundpolicy.exception;

/** 해당 셀러의 환불 정책을 찾을 수 없을 때 예외. */
public class RefundPolicyNotFoundForSellerException extends RefundPolicyException {

    public RefundPolicyNotFoundForSellerException(Long sellerId, Long refundPolicyId) {
        super(
                RefundPolicyErrorCode.REFUND_POLICY_NOT_FOUND_FOR_SELLER,
                String.format(
                        "해당 셀러의 환불 정책을 찾을 수 없습니다 (sellerId: %d, refundPolicyId: %d)",
                        sellerId, refundPolicyId));
    }
}
