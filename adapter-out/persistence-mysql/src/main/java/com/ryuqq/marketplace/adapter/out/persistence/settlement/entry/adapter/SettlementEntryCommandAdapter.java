package com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.mapper.SettlementEntryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.repository.SettlementEntryJpaRepository;
import com.ryuqq.marketplace.application.settlement.entry.port.out.command.SettlementEntryCommandPort;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import java.util.List;
import org.springframework.stereotype.Component;

/** 정산 원장 Command Adapter. */
@Component
public class SettlementEntryCommandAdapter implements SettlementEntryCommandPort {

    private final SettlementEntryJpaRepository repository;
    private final SettlementEntryJpaEntityMapper mapper;

    public SettlementEntryCommandAdapter(
            SettlementEntryJpaRepository repository, SettlementEntryJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persist(SettlementEntry entry) {
        repository.save(mapper.toEntity(entry));
    }

    @Override
    public void persistAll(List<SettlementEntry> entries) {
        repository.saveAll(entries.stream().map(mapper::toEntity).toList());
    }
}
