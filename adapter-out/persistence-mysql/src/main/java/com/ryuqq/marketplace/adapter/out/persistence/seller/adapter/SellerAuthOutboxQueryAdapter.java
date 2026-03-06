package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerAuthOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerAuthOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.seller.port.out.query.SellerAuthOutboxQueryPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * SellerAuthOutboxQueryAdapter - 셀러 인증 Outbox 조회 어댑터.
 *
 * <p>SellerAuthOutboxQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerAuthOutboxQueryAdapter implements SellerAuthOutboxQueryPort {

    private final SellerAuthOutboxQueryDslRepository queryDslRepository;
    private final SellerAuthOutboxJpaEntityMapper mapper;

    public SellerAuthOutboxQueryAdapter(
            SellerAuthOutboxQueryDslRepository queryDslRepository,
            SellerAuthOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public SellerAuthOutbox getById(Long outboxId) {
        Objects.requireNonNull(outboxId, "outboxId must not be null");
        SellerAuthOutboxJpaEntity entity = queryDslRepository.findById(outboxId);
        if (entity == null) {
            throw new IllegalStateException("SellerAuthOutbox를 찾을 수 없습니다. outboxId=" + outboxId);
        }
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<SellerAuthOutbox> findPendingBySellerId(SellerId sellerId) {
        return queryDslRepository.findPendingBySellerId(sellerId.value()).map(mapper::toDomain);
    }

    @Override
    public List<SellerAuthOutbox> findPendingOutboxesForRetry(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<SellerAuthOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
