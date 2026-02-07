package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminAuthOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminAuthOutboxJpaRepository;
import com.ryuqq.marketplace.application.selleradmin.port.out.command.SellerAdminAuthOutboxCommandPort;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import org.springframework.stereotype.Component;

/**
 * SellerAdminAuthOutboxCommandAdapter - 셀러 관리자 인증 Outbox 명령 어댑터.
 *
 * <p>SellerAdminAuthOutboxCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerAdminAuthOutboxCommandAdapter implements SellerAdminAuthOutboxCommandPort {

    private final SellerAdminAuthOutboxJpaRepository repository;
    private final SellerAdminAuthOutboxJpaEntityMapper mapper;

    public SellerAdminAuthOutboxCommandAdapter(
            SellerAdminAuthOutboxJpaRepository repository,
            SellerAdminAuthOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(SellerAdminAuthOutbox outbox) {
        SellerAdminAuthOutboxJpaEntity entity = mapper.toEntity(outbox);
        SellerAdminAuthOutboxJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
