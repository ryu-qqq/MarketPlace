package com.ryuqq.marketplace.adapter.out.persistence.refund.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.refund.mapper.RefundPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimQueryDslRepository;
import com.ryuqq.marketplace.application.claim.port.out.query.ClaimShipmentQueryPort;
import com.ryuqq.marketplace.application.refund.port.out.query.RefundQueryPort;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchCriteria;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.List;
import java.util.Map;
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
                .map(entity -> mapper.toDomain(entity, resolveClaimShipment(entity.getClaimShipmentId())));
    }

    @Override
    public Optional<RefundClaim> findByOrderItemId(String orderItemId) {
        return repository
                .findByOrderItemId(orderItemId)
                .map(entity -> mapper.toDomain(entity, resolveClaimShipment(entity.getClaimShipmentId())));
    }

    @Override
    public List<RefundClaim> findByOrderItemIds(List<String> orderItemIds) {
        return repository.findByOrderItemIds(orderItemIds).stream()
                .map(entity -> mapper.toDomain(entity, resolveClaimShipment(entity.getClaimShipmentId())))
                .toList();
    }

    @Override
    public List<RefundClaim> findByCriteria(RefundSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream()
                .map(entity -> mapper.toDomain(entity, resolveClaimShipment(entity.getClaimShipmentId())))
                .toList();
    }

    @Override
    public long countByCriteria(RefundSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public Map<RefundStatus, Long> countByStatus() {
        return repository.countByStatus();
    }

    @Override
    public List<RefundClaim> findByIdIn(List<String> refundClaimIds, Long sellerId) {
        return repository.findByIdIn(refundClaimIds, sellerId).stream()
                .map(entity -> mapper.toDomain(entity, resolveClaimShipment(entity.getClaimShipmentId())))
                .toList();
    }

    private ClaimShipment resolveClaimShipment(String claimShipmentId) {
        if (claimShipmentId == null) {
            return null;
        }
        return claimShipmentQueryPort.findById(ClaimShipmentId.of(claimShipmentId)).orElse(null);
    }
}
