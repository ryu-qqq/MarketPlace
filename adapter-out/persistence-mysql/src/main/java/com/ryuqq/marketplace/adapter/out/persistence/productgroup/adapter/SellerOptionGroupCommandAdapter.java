package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.SellerOptionGroupJpaRepository;
import com.ryuqq.marketplace.application.productgroup.port.out.command.SellerOptionGroupCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerOptionGroupCommandAdapter - 셀러 옵션 그룹 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class SellerOptionGroupCommandAdapter implements SellerOptionGroupCommandPort {

    private final SellerOptionGroupJpaRepository repository;
    private final ProductGroupJpaEntityMapper mapper;

    public SellerOptionGroupCommandAdapter(
            SellerOptionGroupJpaRepository repository, ProductGroupJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Long> findGroupIdsByProductGroupId(Long productGroupId) {
        return repository.findByProductGroupId(productGroupId).stream()
                .map(SellerOptionGroupJpaEntity::getId)
                .toList();
    }

    @Override
    public void deleteByProductGroupId(Long productGroupId) {
        repository.deleteByProductGroupId(productGroupId);
    }

    @Override
    public Long persist(Long productGroupId, SellerOptionGroup group) {
        SellerOptionGroupJpaEntity entity = mapper.toOptionGroupEntity(group);
        SellerOptionGroupJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
