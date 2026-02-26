package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerSettlementJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerSettlementJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerSettlementJpaRepository;
import com.ryuqq.marketplace.application.seller.port.out.command.SellerSettlementCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerSettlementCommandAdapter - 셀러 정산 정보 명령 어댑터.
 *
 * <p>SellerSettlementCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerSettlementCommandAdapter implements SellerSettlementCommandPort {

    private final SellerSettlementJpaRepository repository;
    private final SellerSettlementJpaEntityMapper mapper;

    public SellerSettlementCommandAdapter(
            SellerSettlementJpaRepository repository, SellerSettlementJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * SellerSettlement 영속화 (생성/수정).
     *
     * @param sellerSettlement 영속화할 SellerSettlement
     * @return 영속화된 SellerSettlement ID
     */
    @Override
    public Long persist(SellerSettlement sellerSettlement) {
        SellerSettlementJpaEntity entity = mapper.toEntity(sellerSettlement);
        SellerSettlementJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    /**
     * SellerSettlement 목록 일괄 영속화.
     *
     * @param sellerSettlements 영속화할 SellerSettlement 목록
     */
    @Override
    public void persistAll(List<SellerSettlement> sellerSettlements) {
        List<SellerSettlementJpaEntity> entities =
                sellerSettlements.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
