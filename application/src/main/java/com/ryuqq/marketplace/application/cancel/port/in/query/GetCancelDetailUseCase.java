package com.ryuqq.marketplace.application.cancel.port.in.query;

import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;

/** 취소 상세 조회 UseCase. */
public interface GetCancelDetailUseCase {
    CancelDetailResult execute(String cancelId);
}
