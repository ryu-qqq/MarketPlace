package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerCsJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerCsJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerCsJpaRepository;
import com.ryuqq.marketplace.application.seller.port.out.command.SellerCsCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerCsCommandAdapter - 셀러 CS 정보 명령 어댑터.
 *
 * <p>SellerCsCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerCsCommandAdapter implements SellerCsCommandPort {

    private final SellerCsJpaRepository repository;
    private final SellerCsJpaEntityMapper mapper;

    public SellerCsCommandAdapter(
            SellerCsJpaRepository repository, SellerCsJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * SellerCs 영속화 (생성/수정).
     *
     * @param sellerCs 영속화할 SellerCs
     * @return 영속화된 SellerCs ID
     */
    @Override
    public Long persist(SellerCs sellerCs) {
        SellerCsJpaEntity entity = mapper.toEntity(sellerCs);
        SellerCsJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    /**
     * SellerCs 목록 일괄 영속화.
     *
     * @param sellerCsList 영속화할 SellerCs 목록
     */
    @Override
    public void persistAll(List<SellerCs> sellerCsList) {
        List<SellerCsJpaEntity> entities = sellerCsList.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
