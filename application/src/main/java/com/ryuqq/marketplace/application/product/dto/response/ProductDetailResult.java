package com.ryuqq.marketplace.application.product.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * 상품(SKU) 상세 조회 결과 DTO.
 *
 * <p>상세 조회에서 각 상품의 옵션 매핑을 그룹명/값명까지 resolved하여 제공합니다.
 *
 * @param id 상품 ID
 * @param skuCode SKU 코드
 * @param regularPrice 정가
 * @param currentPrice 현재가
 * @param salePrice 할인가 (null 가능)
 * @param discountRate 할인율
 * @param stockQuantity 재고수량
 * @param status 상품 상태
 * @param sortOrder 정렬 순서
 * @param options resolved된 옵션 매핑 목록 (옵션 그룹명 + 옵션 값명 포함)
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 */
public record ProductDetailResult(
        Long id,
        String skuCode,
        int regularPrice,
        int currentPrice,
        Integer salePrice,
        int discountRate,
        int stockQuantity,
        String status,
        int sortOrder,
        List<ResolvedProductOptionResult> options,
        Instant createdAt,
        Instant updatedAt) {

    public ProductDetailResult {
        options = options != null ? List.copyOf(options) : List.of();
    }
}
