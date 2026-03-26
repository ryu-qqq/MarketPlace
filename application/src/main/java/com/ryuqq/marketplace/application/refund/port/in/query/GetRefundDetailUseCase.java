package com.ryuqq.marketplace.application.refund.port.in.query;

import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult;

/** 환불 상세 조회 UseCase. */
public interface GetRefundDetailUseCase {
    RefundDetailResult execute(String refundClaimId);
}
