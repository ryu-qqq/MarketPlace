package com.ryuqq.marketplace.application.product.dto.response;

import com.ryuqq.marketplace.domain.product.aggregate.Product;
import java.time.Instant;
import java.util.List;

/** 상품(SKU) 조회 결과 DTO. */
public record ProductResult(
        Long id,
        Long productGroupId,
        String skuCode,
        int regularPrice,
        int currentPrice,
        Integer salePrice,
        int discountRate,
        int stockQuantity,
        String status,
        int sortOrder,
        List<ProductOptionMappingResult> optionMappings,
        Instant createdAt,
        Instant updatedAt) {

    public static ProductResult from(Product product) {
        List<ProductOptionMappingResult> mappings =
                product.optionMappings().stream().map(ProductOptionMappingResult::from).toList();

        return new ProductResult(
                product.idValue(),
                product.productGroupIdValue(),
                product.skuCodeValue(),
                product.regularPriceValue(),
                product.currentPriceValue(),
                product.salePriceValue(),
                product.discountRate(),
                product.stockQuantity(),
                product.status().name(),
                product.sortOrder(),
                mappings,
                product.createdAt(),
                product.updatedAt());
    }
}
