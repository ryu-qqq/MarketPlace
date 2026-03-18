package com.ryuqq.marketplace.application.refund.port.in.query;

import com.ryuqq.marketplace.application.refund.dto.query.RefundSearchParams;
import com.ryuqq.marketplace.application.refund.dto.response.RefundPageResult;

/** 환불 목록 조회 UseCase. */
public interface GetRefundListUseCase {
    RefundPageResult execute(RefundSearchParams params);
}
