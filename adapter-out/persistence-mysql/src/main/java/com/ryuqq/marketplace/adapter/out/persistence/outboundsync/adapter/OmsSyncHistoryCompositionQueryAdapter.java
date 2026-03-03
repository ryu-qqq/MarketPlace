package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.composite.SyncHistoryCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper.OmsSyncHistoryCompositionMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OmsSyncHistoryCompositionQueryDslRepository;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import com.ryuqq.marketplace.application.outboundproduct.port.out.query.OmsSyncHistoryCompositionQueryPort;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 연동 이력 Composition 조회 어댑터.
 *
 * <p>1-pass 전략: 단일 JOIN 쿼리로 outbox + shop + outbound_product 데이터 조회.
 */
@Component
public class OmsSyncHistoryCompositionQueryAdapter implements OmsSyncHistoryCompositionQueryPort {

    private final OmsSyncHistoryCompositionQueryDslRepository compositionRepository;
    private final OmsSyncHistoryCompositionMapper mapper;

    public OmsSyncHistoryCompositionQueryAdapter(
            OmsSyncHistoryCompositionQueryDslRepository compositionRepository,
            OmsSyncHistoryCompositionMapper mapper) {
        this.compositionRepository = compositionRepository;
        this.mapper = mapper;
    }

    @Override
    public List<SyncHistoryListResult> findByCriteria(SyncHistorySearchCriteria criteria) {
        List<SyncHistoryCompositeDto> composites = compositionRepository.findByCriteria(criteria);
        if (composites.isEmpty()) {
            return List.of();
        }
        return mapper.toResults(composites);
    }

    @Override
    public long countByCriteria(SyncHistorySearchCriteria criteria) {
        return compositionRepository.countByCriteria(criteria);
    }
}
