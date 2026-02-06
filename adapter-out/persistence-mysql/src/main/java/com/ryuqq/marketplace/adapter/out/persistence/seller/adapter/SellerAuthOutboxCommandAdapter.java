package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerAuthOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerAuthOutboxJpaRepository;
import com.ryuqq.marketplace.application.seller.port.out.command.SellerAuthOutboxCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import org.springframework.stereotype.Component;

/**
 * SellerAuthOutboxCommandAdapter - 셀러 인증 Outbox 명령 어댑터.
 *
 * <p>SellerAuthOutboxCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerAuthOutboxCommandAdapter implements SellerAuthOutboxCommandPort {

    private final SellerAuthOutboxJpaRepository repository;
    private final SellerAuthOutboxJpaEntityMapper mapper;

    public SellerAuthOutboxCommandAdapter(
            SellerAuthOutboxJpaRepository repository, SellerAuthOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * SellerAuthOutbox 영속화 (생성/수정).
     *
     * @param outbox 영속화할 SellerAuthOutbox
     * @return 영속화된 SellerAuthOutbox ID
     */
    @Override
    public Long persist(SellerAuthOutbox outbox) {
        SellerAuthOutboxJpaEntity entity = mapper.toEntity(outbox);
        SellerAuthOutboxJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
