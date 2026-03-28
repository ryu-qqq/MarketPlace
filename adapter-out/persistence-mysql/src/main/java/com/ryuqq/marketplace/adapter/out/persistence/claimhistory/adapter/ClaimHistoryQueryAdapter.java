package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.mapper.ClaimHistoryPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryQueryDslRepository;
import com.ryuqq.marketplace.application.claimhistory.port.out.query.ClaimHistoryQueryPort;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import java.util.List;
import org.springframework.stereotype.Component;

/** 클레임 이력 Query Adapter. */
@Component
public class ClaimHistoryQueryAdapter implements ClaimHistoryQueryPort {

    private final ClaimHistoryQueryDslRepository claimHistoryRepository;
    private final ClaimHistoryPersistenceMapper mapper;

    public ClaimHistoryQueryAdapter(
            ClaimHistoryQueryDslRepository claimHistoryRepository,
            ClaimHistoryPersistenceMapper mapper) {
        this.claimHistoryRepository = claimHistoryRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ClaimHistory> findByClaimTypeAndClaimId(ClaimType claimType, String claimId) {
        return claimHistoryRepository.findByClaimTypeAndClaimId(claimType.name(), claimId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ClaimHistory> findByClaimTypeAndClaimIds(
            ClaimType claimType, List<String> claimIds) {
        return claimHistoryRepository
                .findByClaimTypeAndClaimIds(claimType.name(), claimIds)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ClaimHistory> findByOrderItemId(Long orderItemId) {
        return claimHistoryRepository.findByOrderItemId(orderItemId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ClaimHistory> findByCriteria(ClaimHistoryPageCriteria criteria) {
        return claimHistoryRepository.findByCriteria(criteria).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countByCriteria(ClaimHistoryPageCriteria criteria) {
        return claimHistoryRepository.countByCriteria(criteria);
    }
}
