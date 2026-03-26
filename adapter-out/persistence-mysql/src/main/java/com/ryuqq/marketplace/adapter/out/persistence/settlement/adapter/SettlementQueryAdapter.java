package com.ryuqq.marketplace.adapter.out.persistence.settlement.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.mapper.SettlementJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.repository.SettlementQueryDslRepository;
import com.ryuqq.marketplace.application.settlement.port.out.query.SettlementQueryPort;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 정산 Query Adapter. */
@Component
public class SettlementQueryAdapter implements SettlementQueryPort {

    private final SettlementQueryDslRepository repository;
    private final SettlementJpaEntityMapper mapper;

    public SettlementQueryAdapter(
            SettlementQueryDslRepository repository, SettlementJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Settlement> findById(SettlementId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Settlement> findBySellerIdAndPeriod(
            long sellerId, LocalDate startDate, LocalDate endDate) {
        return repository
                .findBySellerIdAndPeriod(sellerId, startDate, endDate)
                .map(mapper::toDomain);
    }

    @Override
    public List<Settlement> findBySellerIdAndStatus(long sellerId, SettlementStatus status) {
        return repository.findBySellerIdAndStatus(sellerId, status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Settlement> findByStatus(SettlementStatus status) {
        return repository.findByStatus(status.name()).stream().map(mapper::toDomain).toList();
    }
}
