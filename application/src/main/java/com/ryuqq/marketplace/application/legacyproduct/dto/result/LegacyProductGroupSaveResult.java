package com.ryuqq.marketplace.application.legacyproduct.dto.result;

import java.util.List;

/**
 * 레거시 상품그룹 저장 결과.
 *
 * <p>코디네이터가 luxurydb INSERT 후 반환하는 내부 결과입니다.
 *
 * @param productGroupId luxurydb product_group PK
 * @param productIds luxurydb product PK 목록
 */
public record LegacyProductGroupSaveResult(long productGroupId, List<Long> productIds) {

    public LegacyProductGroupSaveResult {
        productIds = List.copyOf(productIds);
    }
}
