package com.ryuqq.marketplace.application.settlement.port.in.query;

import com.ryuqq.marketplace.application.settlement.dto.query.DailySettlementSearchParams;
import com.ryuqq.marketplace.application.settlement.dto.response.DailySettlementResult;
import java.util.List;

/** 일별 정산 통계 조회 UseCase. */
public interface GetDailySettlementUseCase {

    List<DailySettlementResult> execute(DailySettlementSearchParams params);
}
