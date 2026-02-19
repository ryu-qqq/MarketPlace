package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.SellerOptionValueJpaRepository;
import com.ryuqq.marketplace.application.selleroption.port.out.command.SellerOptionValueCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerOptionValueCommandAdapter - 셀러 옵션 값 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class SellerOptionValueCommandAdapter implements SellerOptionValueCommandPort {

    private final SellerOptionValueJpaRepository repository;
    private final ProductGroupJpaEntityMapper mapper;

    public SellerOptionValueCommandAdapter(
            SellerOptionValueJpaRepository repository, ProductGroupJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(SellerOptionValue value) {
        SellerOptionValueJpaEntity entity = mapper.toOptionValueEntity(value);
        SellerOptionValueJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<SellerOptionValue> values) {
        List<SellerOptionValueJpaEntity> entities =
                values.stream().map(mapper::toOptionValueEntity).toList();
        List<SellerOptionValueJpaEntity> saved = repository.saveAll(entities);
        return saved.stream().map(SellerOptionValueJpaEntity::getId).toList();
    }

    @Override
    public List<Long> persistAllForGroup(Long sellerOptionGroupId, List<SellerOptionValue> values) {
        List<SellerOptionValueJpaEntity> entities =
                values.stream()
                        .map(v -> mapper.toOptionValueEntity(v, sellerOptionGroupId))
                        .toList();
        List<SellerOptionValueJpaEntity> saved = repository.saveAll(entities);
        return saved.stream().map(SellerOptionValueJpaEntity::getId).toList();
    }
}
