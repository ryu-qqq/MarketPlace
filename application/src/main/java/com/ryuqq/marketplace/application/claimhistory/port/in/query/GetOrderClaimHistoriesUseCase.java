package com.ryuqq.marketplace.application.claimhistory.port.in.query;

import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryPageResult;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;

/** 주문 클레임 이력 페이지 조회 UseCase. */
public interface GetOrderClaimHistoriesUseCase {

    ClaimHistoryPageResult execute(ClaimHistoryPageCriteria criteria);
}
