package com.ryuqq.marketplace.domain.legacyconversion.vo;

import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 레거시 → Market PK resolve 결과를 담는 VO.
 *
 * <p>레거시 API 요청의 productGroupId와 productId들을 market PK로 변환한 결과를 불변으로 관리합니다.
 *
 * @param resolvedProductGroupId market 상품그룹 PK
 * @param productIdMap 레거시 productId → market productId 매핑
 */
public record ResolvedLegacyProductIds(
        long requestProductGroupId,
        ProductGroupId resolvedProductGroupId,
        Map<Long, ProductId> productIdMap) {

    public ResolvedLegacyProductIds {
        productIdMap = Map.copyOf(productIdMap);
    }

    /** 레거시 productId를 market ProductId로 변환. 매핑 없으면 그대로 반환. */
    public ProductId resolveProductId(long legacyProductId) {
        return productIdMap.getOrDefault(legacyProductId, ProductId.of(legacyProductId));
    }

    /** 역매핑 (market → legacy) 생성. */
    public Map<Long, Long> reverseProductIdMap() {
        return productIdMap.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getValue().value(), Map.Entry::getKey));
    }
}
