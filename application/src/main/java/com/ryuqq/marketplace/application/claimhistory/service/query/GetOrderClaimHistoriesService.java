package com.ryuqq.marketplace.application.claimhistory.service.query;

import com.ryuqq.marketplace.application.claimhistory.assembler.ClaimHistoryAssembler;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryPageResult;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryReadManager;
import com.ryuqq.marketplace.application.claimhistory.port.in.query.GetOrderClaimHistoriesUseCase;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;
import org.springframework.stereotype.Service;

/** 주문 클레임 이력 페이지 조회 Service. */
@Service
public class GetOrderClaimHistoriesService implements GetOrderClaimHistoriesUseCase {

    private final ClaimHistoryReadManager readManager;
    private final ClaimHistoryAssembler assembler;

    public GetOrderClaimHistoriesService(
            ClaimHistoryReadManager readManager, ClaimHistoryAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public ClaimHistoryPageResult execute(ClaimHistoryPageCriteria criteria) {
        List<ClaimHistory> histories = readManager.findByCriteria(criteria);
        long totalCount = readManager.countByCriteria(criteria);

        List<ClaimHistoryResult> results = assembler.toResults(histories);
        PageMeta pageMeta = PageMeta.of(criteria.page(), criteria.size(), totalCount);

        return ClaimHistoryPageResult.of(results, pageMeta);
    }
}
