package com.ryuqq.marketplace.application.brand.dto.response;

public record BrandResponse(
    Long brandId,
    String code,
    String canonicalName,
    String nameKo,
    String nameEn,
    String shortName,
    String countryCode,
    String department,
    boolean isLuxury,
    String status,
    String logoUrl
) {
}
