package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductOptionQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper.LegacyProductCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository.LegacyProductCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.legacyproduct.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacyproduct.port.out.query.LegacyProductCompositionQueryPort;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB 상품 Composition 조회 Adapter.
 *
 * <p>{@link LegacyProductCompositionQueryPort}의 구현체입니다. 5테이블 조인 쿼리를 실행하고 Mapper를 통해 Application
 * DTO로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LegacyProductCompositionQueryAdapter implements LegacyProductCompositionQueryPort {

    private final LegacyProductCompositeQueryDslRepository repository;
    private final LegacyProductCompositeMapper mapper;

    public LegacyProductCompositionQueryAdapter(
            LegacyProductCompositeQueryDslRepository repository,
            LegacyProductCompositeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<LegacyProductCompositeResult> findProductsByProductGroupId(long productGroupId) {
        List<LegacyProductOptionQueryDto> rows =
                repository.fetchProductsWithOptions(productGroupId);
        return mapper.toCompositeResults(rows);
    }
}
