package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminEmailOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminEmailOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminEmailOutboxJpaRepository;
import com.ryuqq.marketplace.application.selleradmin.port.out.command.SellerAdminEmailOutboxCommandPort;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import org.springframework.stereotype.Component;

/**
 * SellerAdminEmailOutboxCommandAdapter - 셀러 관리자 이메일 Outbox 명령 어댑터.
 *
 * <p>SellerAdminEmailOutboxCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerAdminEmailOutboxCommandAdapter implements SellerAdminEmailOutboxCommandPort {

    private final SellerAdminEmailOutboxJpaRepository repository;
    private final SellerAdminEmailOutboxJpaEntityMapper mapper;

    public SellerAdminEmailOutboxCommandAdapter(
            SellerAdminEmailOutboxJpaRepository repository,
            SellerAdminEmailOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * SellerAdminEmailOutbox 영속화 (생성/수정).
     *
     * @param outbox 영속화할 SellerAdminEmailOutbox
     * @return 영속화된 SellerAdminEmailOutbox ID
     */
    @Override
    public Long persist(SellerAdminEmailOutbox outbox) {
        SellerAdminEmailOutboxJpaEntity entity = mapper.toEntity(outbox);
        SellerAdminEmailOutboxJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
