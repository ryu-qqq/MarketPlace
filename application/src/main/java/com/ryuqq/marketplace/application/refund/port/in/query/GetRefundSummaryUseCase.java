package com.ryuqq.marketplace.application.refund.port.in.query;

import com.ryuqq.marketplace.application.refund.dto.response.RefundSummaryResult;

/** 환불 상태별 요약 조회 UseCase. */
public interface GetRefundSummaryUseCase {
    RefundSummaryResult execute();
}
