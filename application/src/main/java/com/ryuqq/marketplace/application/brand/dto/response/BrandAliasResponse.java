package com.ryuqq.marketplace.application.brand.dto.response;

public record BrandAliasResponse(
    Long aliasId,
    Long brandId,
    String originalAlias,
    String normalizedAlias,
    String sourceType,
    Long sellerId,
    String mallCode,
    double confidenceValue,
    String status
) {
}
