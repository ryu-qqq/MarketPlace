package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.mapper.ClaimHistoryPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.application.claimhistory.port.out.command.ClaimHistoryCommandPort;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import java.util.List;
import org.springframework.stereotype.Component;

/** 클레임 이력 Command Adapter. */
@Component
public class ClaimHistoryCommandAdapter implements ClaimHistoryCommandPort {

    private final ClaimHistoryJpaRepository claimHistoryRepository;
    private final ClaimHistoryPersistenceMapper mapper;

    public ClaimHistoryCommandAdapter(
            ClaimHistoryJpaRepository claimHistoryRepository,
            ClaimHistoryPersistenceMapper mapper) {
        this.claimHistoryRepository = claimHistoryRepository;
        this.mapper = mapper;
    }

    @Override
    public void persist(ClaimHistory history) {
        claimHistoryRepository.save(mapper.toEntity(history));
    }

    @Override
    public void persistAll(List<ClaimHistory> histories) {
        claimHistoryRepository.saveAll(histories.stream().map(mapper::toEntity).toList());
    }
}
