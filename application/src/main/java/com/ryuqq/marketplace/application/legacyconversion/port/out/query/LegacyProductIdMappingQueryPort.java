package com.ryuqq.marketplace.application.legacyconversion.port.out.query;

import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import java.util.List;
import java.util.Optional;

/** 레거시 상품(SKU) ID 매핑 조회 포트. */
public interface LegacyProductIdMappingQueryPort {

    /**
     * 레거시 Product(SKU) ID로 매핑 조회.
     *
     * @param legacyProductId 레거시 Product ID
     * @return 매핑 Optional
     */
    Optional<LegacyProductIdMapping> findByLegacyProductId(long legacyProductId);

    /**
     * 내부 Product ID로 매핑 조회.
     *
     * @param internalProductId 내부 Product ID
     * @return 매핑 Optional
     */
    Optional<LegacyProductIdMapping> findByInternalProductId(long internalProductId);

    /**
     * 레거시 상품그룹 ID로 해당 그룹의 모든 SKU 매핑 조회.
     *
     * @param legacyProductGroupId 레거시 상품그룹 ID
     * @return SKU 매핑 목록
     */
    List<LegacyProductIdMapping> findByLegacyProductGroupId(long legacyProductGroupId);
}
