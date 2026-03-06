package com.ryuqq.marketplace.application.outboundproduct.manager;

import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import com.ryuqq.marketplace.application.outboundproduct.port.out.query.OmsSyncHistoryCompositionQueryPort;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 연동 이력 Composition 조회 ReadManager. */
@Component
public class OmsSyncHistoryCompositionReadManager {

    private final OmsSyncHistoryCompositionQueryPort compositionQueryPort;

    public OmsSyncHistoryCompositionReadManager(
            OmsSyncHistoryCompositionQueryPort compositionQueryPort) {
        this.compositionQueryPort = compositionQueryPort;
    }

    @Transactional(readOnly = true)
    public List<SyncHistoryListResult> findByCriteria(SyncHistorySearchCriteria criteria) {
        return compositionQueryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(SyncHistorySearchCriteria criteria) {
        return compositionQueryPort.countByCriteria(criteria);
    }
}
