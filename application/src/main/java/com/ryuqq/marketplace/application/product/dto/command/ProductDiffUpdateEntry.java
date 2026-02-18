package com.ryuqq.marketplace.application.product.dto.command;

import java.util.List;

/**
 * 전체 수정(FullUpdate) 시 Product diff 매칭용 엔트리.
 *
 * <p>{@code updateWithDiff()}에서 productId 기반으로 기존 Product를 매칭합니다. productId가 null이면 신규, non-null이면
 * 기존 Product를 의미합니다.
 *
 * <p>salePrice와 discountRate는 도메인 내부에서 자동 계산됩니다.
 *
 * @param productId 기존 Product ID (nullable: null이면 신규)
 * @param skuCode SKU 코드
 * @param regularPrice 정가
 * @param currentPrice 판매가
 * @param stockQuantity 재고 수량
 * @param sortOrder 정렬 순서
 * @param optionValueIndices orderedActiveValueIds에 대한 인덱스 목록
 */
public record ProductDiffUpdateEntry(
        Long productId,
        String skuCode,
        int regularPrice,
        int currentPrice,
        int stockQuantity,
        int sortOrder,
        List<Integer> optionValueIndices) {

    public ProductDiffUpdateEntry {
        optionValueIndices = List.copyOf(optionValueIndices);
    }
}
