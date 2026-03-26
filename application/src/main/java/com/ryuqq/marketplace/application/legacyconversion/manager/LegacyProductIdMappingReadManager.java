package com.ryuqq.marketplace.application.legacyconversion.manager;

import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyProductIdMappingQueryPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 레거시 상품(SKU) ID 매핑 조회 Manager. */
@Component
public class LegacyProductIdMappingReadManager {

    private final LegacyProductIdMappingQueryPort queryPort;

    public LegacyProductIdMappingReadManager(LegacyProductIdMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 레거시 Product(SKU) ID로 매핑 조회.
     *
     * @param legacyProductId 레거시 Product ID
     * @return 매핑 Optional
     */
    public Optional<LegacyProductIdMapping> findByLegacyProductId(long legacyProductId) {
        return queryPort.findByLegacyProductId(legacyProductId);
    }

    /**
     * 내부 Product ID로 매핑 조회.
     *
     * @param internalProductId 내부 Product ID
     * @return 매핑 Optional
     */
    public Optional<LegacyProductIdMapping> findByInternalProductId(long internalProductId) {
        return queryPort.findByInternalProductId(internalProductId);
    }

    /**
     * 내부 상품그룹 ID로 해당 그룹의 모든 SKU 매핑 조회.
     *
     * @param internalProductGroupId 내부 상품그룹 ID
     * @return SKU 매핑 목록
     */
    public List<LegacyProductIdMapping> findByInternalProductGroupId(long internalProductGroupId) {
        return queryPort.findByInternalProductGroupId(internalProductGroupId);
    }

    /**
     * 레거시 상품그룹 ID로 해당 그룹의 모든 SKU 매핑 조회.
     *
     * @param legacyProductGroupId 레거시 상품그룹 ID
     * @return SKU 매핑 목록
     */
    public List<LegacyProductIdMapping> findByLegacyProductGroupId(long legacyProductGroupId) {
        return queryPort.findByLegacyProductGroupId(legacyProductGroupId);
    }

    /**
     * 여러 레거시 상품그룹 ID로 매핑 일괄 조회.
     *
     * @param legacyProductGroupIds 레거시 상품그룹 ID 목록
     * @return 매핑 목록
     */
    public List<LegacyProductIdMapping> findByLegacyProductGroupIds(
            Collection<Long> legacyProductGroupIds) {
        return queryPort.findByLegacyProductGroupIds(legacyProductGroupIds);
    }
}
