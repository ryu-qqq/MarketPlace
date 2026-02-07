package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminEmailOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminEmailOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.selleradmin.port.out.query.SellerAdminEmailOutboxQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * SellerAdminEmailOutboxQueryAdapter - 셀러 관리자 이메일 Outbox 조회 어댑터.
 *
 * <p>SellerAdminEmailOutboxQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerAdminEmailOutboxQueryAdapter implements SellerAdminEmailOutboxQueryPort {

    private final SellerAdminEmailOutboxQueryDslRepository queryDslRepository;
    private final SellerAdminEmailOutboxJpaEntityMapper mapper;

    public SellerAdminEmailOutboxQueryAdapter(
            SellerAdminEmailOutboxQueryDslRepository queryDslRepository,
            SellerAdminEmailOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SellerAdminEmailOutbox> findPendingBySellerId(SellerId sellerId) {
        return queryDslRepository.findPendingBySellerId(sellerId.value()).map(mapper::toDomain);
    }

    @Override
    public List<SellerAdminEmailOutbox> findPendingOutboxesForRetry(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<SellerAdminEmailOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
