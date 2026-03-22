package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.mapper.LegacyProductGroupEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupJdbcRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupJpaRepository;
import com.ryuqq.marketplace.application.legacy.productgroup.port.out.command.LegacyProductGroupCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_group Command Adapter.
 *
 * <p>등록은 JpaRepository, 수정은 JdbcRepository로 바로 UPDATE.
 */
@Component
public class LegacyProductGroupCommandAdapter implements LegacyProductGroupCommandPort {

    private final LegacyProductGroupJpaRepository jpaRepository;
    private final LegacyProductGroupJdbcRepository jdbcRepository;
    private final LegacyProductGroupEntityMapper mapper;

    public LegacyProductGroupCommandAdapter(
            LegacyProductGroupJpaRepository jpaRepository,
            LegacyProductGroupJdbcRepository jdbcRepository,
            LegacyProductGroupEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.jdbcRepository = jdbcRepository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ProductGroup productGroup, long regularPrice, long currentPrice) {
        LegacyProductGroupEntity entity = mapper.toEntity(productGroup, regularPrice, currentPrice);
        return jpaRepository.save(entity).getId();
    }

    @Override
    public void persist(ProductGroupUpdateData updateData, long regularPrice, long currentPrice) {
        jdbcRepository.update(
                updateData.productGroupId().value(),
                updateData.productGroupName().value(),
                updateData.brandId().value(),
                updateData.categoryId().value(),
                updateData.optionType().name(),
                regularPrice,
                currentPrice);
    }

    @Override
    public void updateDisplayYn(long productGroupId, String displayYn) {
        jdbcRepository.updateDisplayYn(productGroupId, displayYn);
    }

    @Override
    public void markSoldOut(long productGroupId) {
        jdbcRepository.markSoldOut(productGroupId);
    }

    @Override
    public void updatePrice(long productGroupId, long regularPrice, long currentPrice) {
        jdbcRepository.updatePrice(productGroupId, regularPrice, currentPrice);
    }
}
