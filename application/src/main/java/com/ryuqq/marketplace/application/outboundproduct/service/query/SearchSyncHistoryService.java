package com.ryuqq.marketplace.application.outboundproduct.service.query;

import com.ryuqq.marketplace.application.outboundproduct.dto.query.SyncHistorySearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryPageResult;
import com.ryuqq.marketplace.application.outboundproduct.factory.OmsSyncHistoryQueryFactory;
import com.ryuqq.marketplace.application.outboundproduct.manager.OmsSyncHistoryCompositionReadManager;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchSyncHistoryUseCase;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 상품별 연동 이력 조회 Service. */
@Service
public class SearchSyncHistoryService implements SearchSyncHistoryUseCase {

    private final OmsSyncHistoryCompositionReadManager compositionReadManager;
    private final OmsSyncHistoryQueryFactory queryFactory;

    public SearchSyncHistoryService(
            OmsSyncHistoryCompositionReadManager compositionReadManager,
            OmsSyncHistoryQueryFactory queryFactory) {
        this.compositionReadManager = compositionReadManager;
        this.queryFactory = queryFactory;
    }

    @Override
    public SyncHistoryPageResult execute(SyncHistorySearchParams params) {
        SyncHistorySearchCriteria criteria = queryFactory.createCriteria(params);

        List<SyncHistoryListResult> results = compositionReadManager.findByCriteria(criteria);
        long totalElements = compositionReadManager.countByCriteria(criteria);

        return SyncHistoryPageResult.of(results, criteria.page(), criteria.size(), totalElements);
    }
}
