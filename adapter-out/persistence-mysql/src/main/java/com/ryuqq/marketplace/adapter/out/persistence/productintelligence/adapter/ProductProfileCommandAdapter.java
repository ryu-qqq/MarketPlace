package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.ProductProfileJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper.ProductProfileJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository.ProductProfileJpaRepository;
import com.ryuqq.marketplace.application.productintelligence.port.out.command.ProductProfileCommandPort;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import org.springframework.stereotype.Component;

/**
 * ProductProfileCommandAdapter - 상품 프로파일 명령 어댑터.
 *
 * <p>ProductProfileCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ProductProfileCommandAdapter implements ProductProfileCommandPort {

    private final ProductProfileJpaRepository repository;
    private final ProductProfileJpaEntityMapper mapper;

    public ProductProfileCommandAdapter(
            ProductProfileJpaRepository repository, ProductProfileJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ProductProfile profile) {
        ProductProfileJpaEntity entity = mapper.toEntity(profile);
        ProductProfileJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
