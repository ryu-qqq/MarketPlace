package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminAuthOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminAuthOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.selleradmin.port.out.query.SellerAdminAuthOutboxQueryPort;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * SellerAdminAuthOutboxQueryAdapter - 셀러 관리자 인증 Outbox 조회 어댑터.
 *
 * <p>SellerAdminAuthOutboxQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerAdminAuthOutboxQueryAdapter implements SellerAdminAuthOutboxQueryPort {

    private final SellerAdminAuthOutboxQueryDslRepository queryDslRepository;
    private final SellerAdminAuthOutboxJpaEntityMapper mapper;

    public SellerAdminAuthOutboxQueryAdapter(
            SellerAdminAuthOutboxQueryDslRepository queryDslRepository,
            SellerAdminAuthOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public SellerAdminAuthOutbox getById(Long outboxId) {
        Objects.requireNonNull(outboxId, "outboxId must not be null");
        SellerAdminAuthOutboxJpaEntity entity = queryDslRepository.findById(outboxId);
        if (entity == null) {
            throw new IllegalStateException(
                    "SellerAdminAuthOutbox를 찾을 수 없습니다. outboxId=" + outboxId);
        }
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<SellerAdminAuthOutbox> findPendingBySellerAdminId(SellerAdminId sellerAdminId) {
        return queryDslRepository
                .findPendingBySellerAdminId(sellerAdminId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<SellerAdminAuthOutbox> findPendingOutboxesForRetry(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<SellerAdminAuthOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
