package com.ryuqq.marketplace.application.outboundproduct.port.in.query;

import com.ryuqq.marketplace.application.outboundproduct.dto.query.SyncHistorySearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryPageResult;

/** 상품별 연동 이력 조회 UseCase. */
public interface SearchSyncHistoryUseCase {
    SyncHistoryPageResult execute(SyncHistorySearchParams params);
}
