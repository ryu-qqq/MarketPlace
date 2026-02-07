package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerContractJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerContractJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerContractJpaRepository;
import com.ryuqq.marketplace.application.seller.port.out.command.SellerContractCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerContractCommandAdapter - 셀러 계약 정보 명령 어댑터.
 *
 * <p>SellerContractCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerContractCommandAdapter implements SellerContractCommandPort {

    private final SellerContractJpaRepository repository;
    private final SellerContractJpaEntityMapper mapper;

    public SellerContractCommandAdapter(
            SellerContractJpaRepository repository, SellerContractJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * SellerContract 영속화 (생성/수정).
     *
     * @param sellerContract 영속화할 SellerContract
     * @return 영속화된 SellerContract ID
     */
    @Override
    public Long persist(SellerContract sellerContract) {
        SellerContractJpaEntity entity = mapper.toEntity(sellerContract);
        SellerContractJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    /**
     * SellerContract 목록 일괄 영속화.
     *
     * @param sellerContracts 영속화할 SellerContract 목록
     */
    @Override
    public void persistAll(List<SellerContract> sellerContracts) {
        List<SellerContractJpaEntity> entities =
                sellerContracts.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
