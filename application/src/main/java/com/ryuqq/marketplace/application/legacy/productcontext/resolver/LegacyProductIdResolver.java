package com.ryuqq.marketplace.application.legacy.productcontext.resolver;

import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyProductIdMappingReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 ID ↔ Market 상품 ID 양방향 리졸버.
 *
 * <p>레거시 PK 범위의 ID가 요청에 들어오면 매핑 테이블에서 market PK로 변환합니다. 매핑이 없으면 이미 market PK이므로 그대로 반환합니다.
 *
 * <p>응답 시에는 역방향으로 market PK → 레거시 PK 변환을 수행합니다.
 */
@Component
public class LegacyProductIdResolver {

    private final LegacyProductIdMappingReadManager mappingReadManager;

    public LegacyProductIdResolver(LegacyProductIdMappingReadManager mappingReadManager) {
        this.mappingReadManager = mappingReadManager;
    }

    /**
     * 요청의 productGroupId를 market PK로 변환.
     *
     * <p>레거시 productGroupId로 매핑 테이블을 조회합니다. 매핑이 있으면 market PK, 없으면 이미 market PK이므로 그대로 반환합니다.
     *
     * @param requestProductGroupId 레거시 또는 market productGroupId
     * @return market productGroupId
     */
    public long resolveProductGroupId(long requestProductGroupId) {
        List<LegacyProductIdMapping> mappings =
                mappingReadManager.findByLegacyProductGroupId(requestProductGroupId);
        return mappings.isEmpty()
                ? requestProductGroupId
                : mappings.getFirst().internalProductGroupId();
    }

    /**
     * 요청의 productId(SKU)를 market PK로 변환.
     *
     * @param requestProductId 레거시 또는 market productId
     * @return market productId
     */
    public long resolveProductId(long requestProductId) {
        return mappingReadManager
                .findByLegacyProductId(requestProductId)
                .map(LegacyProductIdMapping::internalProductId)
                .orElse(requestProductId);
    }

    /**
     * 레거시 productGroupId로 해당 그룹의 모든 SKU 매핑을 조회하여 Map으로 반환.
     *
     * @param legacyProductGroupId 레거시 productGroupId
     * @return legacyProductId → internalProductId 매핑
     */
    public Map<Long, Long> resolveProductIdsByLegacyGroupId(long legacyProductGroupId) {
        List<LegacyProductIdMapping> mappings =
                mappingReadManager.findByLegacyProductGroupId(legacyProductGroupId);
        return mappings.stream()
                .collect(
                        Collectors.toMap(
                                LegacyProductIdMapping::legacyProductId,
                                LegacyProductIdMapping::internalProductId));
    }

    /**
     * market productGroupId → 레거시 productGroupId 역매핑 (응답용).
     *
     * <p>매핑이 없으면 market PK 그대로 반환 (신규 등록 상품).
     *
     * @param internalProductGroupId market productGroupId
     * @return 레거시 또는 market productGroupId
     */
    public long reverseResolveProductGroupId(long internalProductGroupId) {
        List<LegacyProductIdMapping> mappings =
                mappingReadManager.findByInternalProductGroupId(internalProductGroupId);
        return mappings.isEmpty()
                ? internalProductGroupId
                : mappings.getFirst().legacyProductGroupId();
    }

    /**
     * market productId → 레거시 productId 역매핑 (응답용).
     *
     * <p>매핑이 없으면 market PK 그대로 반환 (신규 등록 상품).
     *
     * @param internalProductId market productId
     * @return 레거시 또는 market productId
     */
    public long reverseResolveProductId(long internalProductId) {
        return mappingReadManager
                .findByInternalProductId(internalProductId)
                .map(LegacyProductIdMapping::legacyProductId)
                .orElse(internalProductId);
    }

    /**
     * market productGroupId로 해당 그룹의 모든 SKU 역매핑을 조회하여 Map으로 반환.
     *
     * @param internalProductGroupId market productGroupId
     * @return internalProductId → legacyProductId 매핑
     */
    public Map<Long, Long> reverseResolveProductIdsByInternalGroupId(long internalProductGroupId) {
        List<LegacyProductIdMapping> mappings =
                mappingReadManager.findByInternalProductGroupId(internalProductGroupId);
        return mappings.stream()
                .collect(
                        Collectors.toMap(
                                LegacyProductIdMapping::internalProductId,
                                LegacyProductIdMapping::legacyProductId));
    }
}
