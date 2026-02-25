package com.ryuqq.marketplace.application.legacyproduct.dto.composite;

import java.util.List;

/**
 * 세토프 DB 상품그룹 상세 조회 번들.
 *
 * <p>ReadFacade → Service 전달용 중간 DTO입니다.
 *
 * @param composite 상품그룹 Composite 기본 정보
 * @param products 상품+옵션+재고 정보
 */
public record LegacyProductGroupDetailBundle(
        LegacyProductGroupCompositeResult composite, List<LegacyProductCompositeResult> products) {

    public static LegacyProductGroupDetailBundle of(
            LegacyProductGroupCompositeResult composite,
            List<LegacyProductCompositeResult> products) {
        return new LegacyProductGroupDetailBundle(composite, products);
    }
}
