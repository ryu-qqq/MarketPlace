package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupBasicQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupImageQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper.LegacyProductGroupCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository.LegacyProductGroupDetailQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.productgroup.port.out.query.LegacyProductGroupCompositionQueryPort;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB 상품그룹 Composition 조회 Adapter.
 *
 * <p>{@link LegacyProductGroupCompositionQueryPort}의 구현체입니다. 7개 테이블 JOIN 쿼리로 기본 정보를 한 번에 조회하고, 이미지만
 * 별도 쿼리로 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LegacyProductGroupCompositionQueryAdapter
        implements LegacyProductGroupCompositionQueryPort {

    private final LegacyProductGroupDetailQueryDslRepository detailQueryDslRepository;
    private final LegacyProductGroupCompositeMapper mapper;

    public LegacyProductGroupCompositionQueryAdapter(
            LegacyProductGroupDetailQueryDslRepository detailQueryDslRepository,
            LegacyProductGroupCompositeMapper mapper) {
        this.detailQueryDslRepository = detailQueryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<LegacyProductGroupCompositeResult> findCompositeById(long productGroupId) {
        Optional<LegacyProductGroupBasicQueryDto> basicOpt =
                detailQueryDslRepository.fetchBasicInfo(productGroupId);

        if (basicOpt.isEmpty()) {
            return Optional.empty();
        }

        List<LegacyProductGroupImageQueryDto> images =
                detailQueryDslRepository.fetchImages(productGroupId);

        return Optional.of(mapper.toCompositeResult(basicOpt.get(), images));
    }
}
