package com.ryuqq.marketplace.application.productgroup.dto.composite;

import java.util.List;
import java.util.Map;

/**
 * 엑셀용 기본 데이터 번들.
 *
 * <p>base composite + 가격 enrichment + 옵션 요약 + description cdnUrl을 한 번의 조회 흐름으로 구성한 결과.
 *
 * @param composites 기본 목록 데이터 (enrichment 적용 완료)
 * @param descriptionCdnUrlByProductGroupId 상품 그룹별 상세설명 CDN URL
 * @param totalElements 전체 건수
 */
public record ProductGroupExcelBaseBundle(
        List<ProductGroupListCompositeResult> composites,
        Map<Long, String> descriptionCdnUrlByProductGroupId,
        long totalElements) {

    public ProductGroupExcelBaseBundle {
        composites = composites != null ? List.copyOf(composites) : List.of();
        descriptionCdnUrlByProductGroupId =
                descriptionCdnUrlByProductGroupId != null
                        ? Map.copyOf(descriptionCdnUrlByProductGroupId)
                        : Map.of();
    }
}
