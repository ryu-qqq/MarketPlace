package com.ryuqq.marketplace.adapter.out.persistence.exchange.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.mapper.ExchangePersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeClaimQueryDslRepository;
import com.ryuqq.marketplace.application.claim.port.out.query.ClaimShipmentQueryPort;
import com.ryuqq.marketplace.application.exchange.port.out.query.ExchangeQueryPort;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import java.util.Map;
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
        return repository.findById(id.value()).map(this::toDomainWithShipment);
    }

    @Override
    public Optional<ExchangeClaim> findByOrderItemId(OrderItemId orderItemId) {
        return repository.findByOrderItemId(orderItemId.value()).map(this::toDomainWithShipment);
    }

    @Override
    public List<ExchangeClaim> findAllByOrderItemId(OrderItemId orderItemId) {
        return repository.findAllByOrderItemId(orderItemId.value()).stream()
                .map(this::toDomainWithShipment)
                .toList();
    }

    @Override
    public List<ExchangeClaim> findByOrderItemIds(List<OrderItemId> orderItemIds) {
        List<Long> ids = orderItemIds.stream().map(OrderItemId::value).toList();
        return repository.findByOrderItemIds(ids).stream().map(this::toDomainWithShipment).toList();
    }

    @Override
    public List<ExchangeClaim> findByCriteria(ExchangeSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream()
                .map(this::toDomainWithShipment)
                .toList();
    }

    @Override
    public long countByCriteria(ExchangeSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public Map<ExchangeStatus, Long> countByStatus() {
        return repository.countByStatus();
    }

    @Override
    public List<ExchangeClaim> findByIdIn(List<String> exchangeClaimIds, Long sellerId) {
        return repository.findByIdIn(exchangeClaimIds, sellerId).stream()
                .map(this::toDomainWithShipment)
                .toList();
    }

    private ExchangeClaim toDomainWithShipment(ExchangeClaimJpaEntity entity) {
        ClaimShipment collectShipment = resolveClaimShipment(entity.getClaimShipmentId());
        return mapper.toDomain(entity, collectShipment);
    }

    private ClaimShipment resolveClaimShipment(String claimShipmentId) {
        if (claimShipmentId == null) {
            return null;
        }
        return claimShipmentQueryPort.findById(ClaimShipmentId.of(claimShipmentId)).orElse(null);
    }
}
