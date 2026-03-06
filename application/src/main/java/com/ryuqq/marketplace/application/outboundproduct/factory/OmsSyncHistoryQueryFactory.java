package com.ryuqq.marketplace.application.outboundproduct.factory;

import com.ryuqq.marketplace.application.outboundproduct.dto.query.SyncHistorySearchParams;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySearchCriteria;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySortKey;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import org.springframework.stereotype.Component;

/** OMS 연동 이력 Query Factory. SyncHistorySearchParams → SyncHistorySearchCriteria 변환. */
@Component
public class OmsSyncHistoryQueryFactory {

    public SyncHistorySearchCriteria createCriteria(SyncHistorySearchParams params) {
        QueryContext<SyncHistorySortKey> queryContext =
                params.commonSearchParams().toQueryContext(SyncHistorySortKey.class);
        SyncStatus statusFilter = parseStatus(params.status());

        return new SyncHistorySearchCriteria(params.productGroupId(), statusFilter, queryContext);
    }

    private SyncStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return SyncStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
