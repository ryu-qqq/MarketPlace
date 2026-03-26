package com.ryuqq.marketplace.application.cancel.port.in.query;

import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;

/** 취소 상태별 요약 조회 UseCase. */
public interface GetCancelSummaryUseCase {
    CancelSummaryResult execute();
}
