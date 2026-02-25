package com.ryuqq.marketplace.application.legacyproduct.dto.command;

import java.util.List;

/**
 * 레거시 상품 옵션/상품 수정 Command.
 *
 * <p>세토프 PK(productGroupId)와 SKU별 옵션 엔트리를 flat 구조로 전달합니다. 내부 시스템 Command에 대한 의존이 없습니다.
 *
 * @param productGroupId 세토프 상품그룹 PK
 * @param skus SKU(상품) 단위 엔트리 목록
 */
public record LegacyUpdateProductsCommand(long productGroupId, List<SkuEntry> skus) {

    /** 하나의 SKU(상품) 엔트리. */
    public record SkuEntry(
            Long productId, int stockQuantity, long additionalPrice, List<OptionEntry> options) {}

    /** 하나의 옵션 엔트리. */
    public record OptionEntry(
            Long optionGroupId, Long optionDetailId, String optionName, String optionValue) {}
}
