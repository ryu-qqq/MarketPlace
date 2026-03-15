package com.ryuqq.marketplace.adapter.out.persistence.refund.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refund.mapper.RefundPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimQueryDslRepository;
import com.ryuqq.marketplace.application.claim.port.out.query.ClaimShipmentQueryPort;
import com.ryuqq.marketplace.application.refund.port.out.query.RefundQueryPort;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 환불 클레임 Query Adapter. */
@Component
public class RefundQueryAdapter implements RefundQueryPort {

    private final RefundClaimQueryDslRepository repository;
    private final RefundPersistenceMapper mapper;
    private final ClaimShipmentQueryPort claimShipmentQueryPort;

    public RefundQueryAdapter(
            RefundClaimQueryDslRepository repository,
            RefundPersistenceMapper mapper,
            ClaimShipmentQueryPort claimShipmentQueryPort) {
        this.repository = repository;
        this.mapper = mapper;
        this.claimShipmentQueryPort = claimShipmentQueryPort;
    }

    @Override
    public Optional<RefundClaim> findById(RefundClaimId id) {
        return repository
                .findById(id.value())
                .map(
                        entity -> {
                            List<RefundItemJpaEntity> items =
                                    repository.findItemsByClaimId(entity.getId());
                            ClaimShipment collectShipment =
                                    resolveClaimShipment(entity.getClaimShipmentId());
                            return mapper.toDomain(entity, items, collectShipment);
                        });
    }

    @Override
    public Optional<RefundClaim> findByOrderId(String orderId) {
        return repository
                .findByOrderId(orderId)
                .map(
                        entity -> {
                            List<RefundItemJpaEntity> items =
                                    repository.findItemsByClaimId(entity.getId());
                            ClaimShipment collectShipment =
                                    resolveClaimShipment(entity.getClaimShipmentId());
                            return mapper.toDomain(entity, items, collectShipment);
                        });
    }

    @Override
    public List<RefundClaim> findByOrderIds(List<String> orderIds) {
        List<RefundClaimJpaEntity> claimEntities = repository.findByOrderIds(orderIds);
        if (claimEntities.isEmpty()) {
            return List.of();
        }
        List<String> claimIds = claimEntities.stream().map(RefundClaimJpaEntity::getId).toList();
        List<RefundItemJpaEntity> allItems = repository.findItemsByClaimIds(claimIds);

        return claimEntities.stream()
                .map(
                        entity -> {
                            List<RefundItemJpaEntity> items =
                                    allItems.stream()
                                            .filter(
                                                    i ->
                                                            entity.getId()
                                                                    .equals(i.getRefundClaimId()))
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
