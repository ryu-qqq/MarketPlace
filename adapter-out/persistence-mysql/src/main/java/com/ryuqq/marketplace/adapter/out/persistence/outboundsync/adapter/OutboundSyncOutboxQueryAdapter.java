package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.adapter;

import com.querydsl.core.Tuple;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper.OutboundSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.outboundsync.port.out.query.OutboundSyncOutboxQueryPort;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatusSummary;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** 외부 상품 연동 Outbox 조회 어댑터. */
@Component
public class OutboundSyncOutboxQueryAdapter implements OutboundSyncOutboxQueryPort {

    private final OutboundSyncOutboxQueryDslRepository queryDslRepository;
    private final OutboundSyncOutboxJpaEntityMapper mapper;

    public OutboundSyncOutboxQueryAdapter(
            OutboundSyncOutboxQueryDslRepository queryDslRepository,
            OutboundSyncOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<OutboundSyncOutbox> findPendingByProductGroupId(ProductGroupId productGroupId) {
        return queryDslRepository.findPendingByProductGroupId(productGroupId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<OutboundSyncOutbox> findPendingByProductGroupIds(
            java.util.Collection<ProductGroupId> productGroupIds) {
        java.util.List<Long> rawIds = productGroupIds.stream().map(ProductGroupId::value).toList();
        return queryDslRepository.findPendingByProductGroupIds(rawIds).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<OutboundSyncOutbox> findPendingOutboxes(Instant beforeTime, int batchSize) {
        return queryDslRepository.findPendingOutboxes(beforeTime, batchSize).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<OutboundSyncOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutBefore, int batchSize) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public OutboundSyncOutbox getById(Long outboxId) {
        Objects.requireNonNull(outboxId, "outboxId must not be null");
        OutboundSyncOutboxJpaEntity entity = queryDslRepository.findById(outboxId);
        if (entity == null) {
            throw new IllegalStateException("OutboundSyncOutbox를 찾을 수 없습니다. outboxId=" + outboxId);
        }
        return mapper.toDomain(entity);
    }

    @Override
    public List<OutboundSyncOutbox> findActiveByProductGroupIdAndSyncType(
            ProductGroupId productGroupId, SyncType syncType) {
        return queryDslRepository
                .findActiveByProductGroupIdAndSyncType(productGroupId.value(), syncType.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<OutboundSyncOutbox> findPagedByProductGroupId(
            Long productGroupId, SyncStatus status, long offset, int limit) {
        String statusName = status != null ? status.name() : null;
        return queryDslRepository
                .findPagedByProductGroupId(productGroupId, statusName, offset, limit)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countByProductGroupIdAndStatus(Long productGroupId, SyncStatus status) {
        String statusName = status != null ? status.name() : null;
        return queryDslRepository.countByProductGroupIdAndStatus(productGroupId, statusName);
    }

    @Override
    public SyncStatusSummary getSyncSummary(Long productGroupId) {
        List<Tuple> tuples = queryDslRepository.countGroupByStatusForProductGroupId(productGroupId);
        Instant lastSyncAt = resolveLastSyncAt(productGroupId);
        return mapper.toSyncStatusSummary(tuples, lastSyncAt);
    }

    private Instant resolveLastSyncAt(Long productGroupId) {
        List<OutboundSyncOutboxJpaEntity> recentCompleted =
                queryDslRepository.findPagedByProductGroupId(productGroupId, "COMPLETED", 0, 1);
        if (recentCompleted.isEmpty()) {
            return null;
        }
        return recentCompleted.getFirst().getProcessedAt();
    }
}
