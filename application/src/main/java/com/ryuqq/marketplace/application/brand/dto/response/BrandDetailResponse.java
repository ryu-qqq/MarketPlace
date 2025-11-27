package com.ryuqq.marketplace.application.brand.dto.response;

import java.util.List;

public record BrandDetailResponse(
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
    String officialWebsite,
    String logoUrl,
    String description,
    String dataQualityLevel,
    double dataQualityScore,
    int aliasCount,
    List<BrandAliasResponse> aliases
) {
}
