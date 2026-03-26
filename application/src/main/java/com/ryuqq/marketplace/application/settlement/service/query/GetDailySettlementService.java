package com.ryuqq.marketplace.application.settlement.service.query;

import com.ryuqq.marketplace.application.settlement.dto.query.DailySettlementSearchParams;
import com.ryuqq.marketplace.application.settlement.dto.response.DailySettlementResult;
import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryReadManager;
import com.ryuqq.marketplace.application.settlement.port.in.query.GetDailySettlementUseCase;
import java.util.List;
import org.springframework.stereotype.Service;

/** 일별 정산 통계 조회 서비스. */
@Service
public class GetDailySettlementService implements GetDailySettlementUseCase {

    private final SettlementEntryReadManager readManager;

    public GetDailySettlementService(SettlementEntryReadManager readManager) {
        this.readManager = readManager;
    }

    @Override
    public List<DailySettlementResult> execute(DailySettlementSearchParams params) {
        return readManager.aggregateByDate(
                params.startDate(), params.endDate(), params.sellerIds());
    }
}
