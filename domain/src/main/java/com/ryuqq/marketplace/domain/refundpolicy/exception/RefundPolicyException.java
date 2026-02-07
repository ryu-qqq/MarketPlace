package com.ryuqq.marketplace.domain.refundpolicy.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 환불 정책 도메인 예외. */
public class RefundPolicyException extends DomainException {

    public RefundPolicyException(RefundPolicyErrorCode errorCode) {
        super(errorCode);
    }

    public RefundPolicyException(RefundPolicyErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public RefundPolicyException(RefundPolicyErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public static RefundPolicyException policyNotFound() {
        return new RefundPolicyException(RefundPolicyErrorCode.REFUND_POLICY_NOT_FOUND);
    }

    public static RefundPolicyException policyInactive() {
        return new RefundPolicyException(RefundPolicyErrorCode.REFUND_POLICY_INACTIVE);
    }

    public static RefundPolicyException returnPeriodExpired() {
        return new RefundPolicyException(RefundPolicyErrorCode.RETURN_PERIOD_EXPIRED);
    }

    public static RefundPolicyException exchangePeriodExpired() {
        return new RefundPolicyException(RefundPolicyErrorCode.EXCHANGE_PERIOD_EXPIRED);
    }
}
