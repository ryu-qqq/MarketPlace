package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerBusinessInfoJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerBusinessInfoJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerBusinessInfoJpaRepository;
import com.ryuqq.marketplace.application.seller.port.out.command.SellerBusinessInfoCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerBusinessInfoCommandAdapter - 셀러 사업자 정보 명령 어댑터.
 *
 * <p>SellerBusinessInfoCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerBusinessInfoCommandAdapter implements SellerBusinessInfoCommandPort {

    private final SellerBusinessInfoJpaRepository repository;
    private final SellerBusinessInfoJpaEntityMapper mapper;

    public SellerBusinessInfoCommandAdapter(
            SellerBusinessInfoJpaRepository repository, SellerBusinessInfoJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * SellerBusinessInfo 영속화 (생성/수정).
     *
     * @param businessInfo 영속화할 SellerBusinessInfo
     * @return 영속화된 SellerBusinessInfo ID
     */
    @Override
    public Long persist(SellerBusinessInfo businessInfo) {
        SellerBusinessInfoJpaEntity entity = mapper.toEntity(businessInfo);
        SellerBusinessInfoJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    /**
     * SellerBusinessInfo 목록 일괄 영속화.
     *
     * @param businessInfos 영속화할 SellerBusinessInfo 목록
     */
    @Override
    public void persistAll(List<SellerBusinessInfo> businessInfos) {
        List<SellerBusinessInfoJpaEntity> entities =
                businessInfos.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
