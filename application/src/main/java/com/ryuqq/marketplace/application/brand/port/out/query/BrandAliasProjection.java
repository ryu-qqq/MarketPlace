package com.ryuqq.marketplace.application.brand.port.out.query;

public record BrandAliasProjection(
    Long aliasId,
    Long brandId,
    String originalAlias,
    String normalizedAlias,
    String sourceType,
    Long sellerId,
    String mallCode,
    double confidence,
    String status
) {}
