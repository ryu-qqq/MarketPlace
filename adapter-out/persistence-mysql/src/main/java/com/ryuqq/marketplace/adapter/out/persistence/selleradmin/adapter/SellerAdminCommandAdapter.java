package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminJpaRepository;
import com.ryuqq.marketplace.application.selleradmin.port.out.command.SellerAdminCommandPort;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import org.springframework.stereotype.Component;

/**
 * SellerAdminCommandAdapter - 셀러 관리자 명령 어댑터.
 *
 * <p>SellerAdminCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerAdminCommandAdapter implements SellerAdminCommandPort {

    private final SellerAdminJpaRepository repository;
    private final SellerAdminJpaEntityMapper mapper;

    public SellerAdminCommandAdapter(
            SellerAdminJpaRepository repository, SellerAdminJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * SellerAdmin 영속화 (생성/수정).
     *
     * @param sellerAdmin 영속화할 SellerAdmin
     * @return 영속화된 SellerAdmin ID (UUIDv7 String)
     */
    @Override
    public String persist(SellerAdmin sellerAdmin) {
        SellerAdminJpaEntity entity = mapper.toEntity(sellerAdmin);
        SellerAdminJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
