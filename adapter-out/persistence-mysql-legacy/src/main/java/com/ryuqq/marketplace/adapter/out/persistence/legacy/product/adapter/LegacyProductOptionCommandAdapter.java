package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductOptionJpaRepository;
import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyProductOptionCommandPort;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_option INSERT/UPDATE Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyProductOptionCommandAdapter implements LegacyProductOptionCommandPort {

    private final LegacyProductOptionJpaRepository repository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyProductOptionCommandAdapter(
            LegacyProductOptionJpaRepository repository, LegacyProductCommandEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persist(LegacyProductOption productOption) {
        repository.save(mapper.toEntity(productOption));
    }

    @Override
    public void persistAll(List<LegacyProductOption> productOptions) {
        List<LegacyProductOptionEntity> entities =
                productOptions.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
