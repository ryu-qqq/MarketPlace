package com.ryuqq.marketplace.application.cancel.port.in.query;

import com.ryuqq.marketplace.application.cancel.dto.query.CancelSearchParams;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelPageResult;

/** 취소 목록 조회 UseCase. */
public interface GetCancelListUseCase {
    CancelPageResult execute(CancelSearchParams params);
}
