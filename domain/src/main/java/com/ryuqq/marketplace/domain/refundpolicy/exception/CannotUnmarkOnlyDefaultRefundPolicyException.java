package com.ryuqq.marketplace.domain.refundpolicy.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/**
 * 유일한 기본 환불 정책을 해제하려 할 때 발생하는 예외.
 *
 * <p><b>POL-DEF-001</b>: 셀러당 기본 정책은 정확히 1개만 존재해야 함
 */
public class CannotUnmarkOnlyDefaultRefundPolicyException extends DomainException {

    public CannotUnmarkOnlyDefaultRefundPolicyException() {
        super(RefundPolicyErrorCode.CANNOT_UNMARK_ONLY_DEFAULT_POLICY);
    }
}
