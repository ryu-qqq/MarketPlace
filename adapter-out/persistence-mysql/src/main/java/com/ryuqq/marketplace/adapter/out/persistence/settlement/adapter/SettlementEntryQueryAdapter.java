package com.ryuqq.marketplace.adapter.out.persistence.settlement.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.dto.DailySettlementProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.mapper.SettlementEntryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.repository.SettlementEntryQueryDslRepository;
import com.ryuqq.marketplace.application.settlement.dto.response.DailySettlementResult;
import com.ryuqq.marketplace.application.settlement.entry.dto.query.SettlementEntrySearchParams;
import com.ryuqq.marketplace.application.settlement.entry.port.out.query.SettlementEntryQueryPort;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 정산 원장 Query Adapter. */
@Component
public class SettlementEntryQueryAdapter implements SettlementEntryQueryPort {

    private final SettlementEntryQueryDslRepository repository;
    private final SettlementEntryJpaEntityMapper mapper;

    public SettlementEntryQueryAdapter(
            SettlementEntryQueryDslRepository repository, SettlementEntryJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SettlementEntry> findById(SettlementEntryId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<SettlementEntry> findConfirmableEntries(Instant cutoffTime, int limit) {
        return repository.findConfirmableEntries(cutoffTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<SettlementEntry> findBySellerIdAndStatus(long sellerId, EntryStatus status) {
        return repository.findBySellerIdAndStatus(sellerId, status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<SettlementEntry> findByOrderItemId(String orderItemId) {
        return repository.findByOrderItemId(orderItemId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<SettlementEntry> findByIdIn(List<String> entryIds) {
        return repository.findByIdIn(entryIds).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<SettlementEntry> findByCriteria(SettlementEntrySearchParams params) {
        int offset = params.page() * params.size();
        return repository
                .findByCriteria(params.statuses(), params.sellerIds(), offset, params.size())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countByCriteria(SettlementEntrySearchParams params) {
        return repository.countByCriteria(params.statuses(), params.sellerIds());
    }

    @Override
    public List<DailySettlementResult> aggregateByDate(
            LocalDate startDate, LocalDate endDate, List<Long> sellerIds) {
        return repository.aggregateByDate(startDate, endDate, sellerIds).stream()
                .map(this::toDailyResult)
                .toList();
    }

    @Override
    public List<Long> findDistinctSellerIdsByStatus(EntryStatus status) {
        return repository.findDistinctSellerIdsByStatus(status.name());
    }

    private DailySettlementResult toDailyResult(DailySettlementProjectionDto dto) {
        return new DailySettlementResult(
                dto.settlementDay(),
                dto.entryCount() != null ? dto.entryCount() : 0L,
                dto.totalSalesAmount() != null ? dto.totalSalesAmount() : 0,
                dto.totalCommissionAmount() != null ? dto.totalCommissionAmount() : 0,
                dto.totalSettlementAmount() != null ? dto.totalSettlementAmount() : 0);
    }
}
