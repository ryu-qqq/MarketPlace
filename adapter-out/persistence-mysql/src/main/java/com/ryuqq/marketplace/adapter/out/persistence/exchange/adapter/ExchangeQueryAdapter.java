package com.ryuqq.marketplace.adapter.out.persistence.exchange.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.mapper.ExchangePersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeClaimQueryDslRepository;
import com.ryuqq.marketplace.application.claim.port.out.query.ClaimShipmentQueryPort;
import com.ryuqq.marketplace.application.exchange.port.out.query.ExchangeQueryPort;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 교환 클레임 Query Adapter. */
@Component
public class ExchangeQueryAdapter implements ExchangeQueryPort {

    private final ExchangeClaimQueryDslRepository repository;
    private final ExchangePersistenceMapper mapper;
    private final ClaimShipmentQueryPort claimShipmentQueryPort;

    public ExchangeQueryAdapter(
            ExchangeClaimQueryDslRepository repository,
            ExchangePersistenceMapper mapper,
            ClaimShipmentQueryPort claimShipmentQueryPort) {
        this.repository = repository;
        this.mapper = mapper;
        this.claimShipmentQueryPort = claimShipmentQueryPort;
    }

    @Override
    public Optional<ExchangeClaim> findById(ExchangeClaimId id) {
        return repository
                .findById(id.value())
                .map(
                        entity -> {
                            List<ExchangeItemJpaEntity> items =
                                    repository.findItemsByClaimId(entity.getId());
                            ClaimShipment collectShipment =
                                    resolveClaimShipment(entity.getClaimShipmentId());
                            return mapper.toDomain(entity, items, collectShipment);
                        });
    }

    @Override
    public Optional<ExchangeClaim> findByOrderId(String orderId) {
        return repository
                .findByOrderId(orderId)
                .map(
                        entity -> {
                            List<ExchangeItemJpaEntity> items =
                                    repository.findItemsByClaimId(entity.getId());
                            ClaimShipment collectShipment =
                                    resolveClaimShipment(entity.getClaimShipmentId());
                            return mapper.toDomain(entity, items, collectShipment);
                        });
    }

    @Override
    public List<ExchangeClaim> findByOrderIds(List<String> orderIds) {
        List<ExchangeClaimJpaEntity> claimEntities = repository.findByOrderIds(orderIds);
        if (claimEntities.isEmpty()) {
            return List.of();
        }
        List<String> claimIds = claimEntities.stream().map(ExchangeClaimJpaEntity::getId).toList();
        List<ExchangeItemJpaEntity> allItems = repository.findItemsByClaimIds(claimIds);

        return claimEntities.stream()
                .map(
                        entity -> {
                            List<ExchangeItemJpaEntity> items =
                                    allItems.stream()
                                            .filter(
                                                    i ->
                                                            entity.getId()
                                                                    .equals(i.getExchangeClaimId()))
                                            .toList();
                            ClaimShipment collectShipment =
                                    resolveClaimShipment(entity.getClaimShipmentId());
                            return mapper.toDomain(entity, items, collectShipment);
                        })
                .toList();
    }

    private ClaimShipment resolveClaimShipment(String claimShipmentId) {
        if (claimShipmentId == null) {
            return null;
        }
        return claimShipmentQueryPort.findById(ClaimShipmentId.of(claimShipmentId)).orElse(null);
    }
}
