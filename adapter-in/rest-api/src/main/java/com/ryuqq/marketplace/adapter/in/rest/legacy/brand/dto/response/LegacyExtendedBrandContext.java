package com.ryuqq.marketplace.adapter.in.rest.legacy.brand.dto.response;

/** 세토프 ExtendedBrandContext 호환 응답 DTO. */
public record LegacyExtendedBrandContext(
        long brandId,
        String brandName,
        String mainDisplayType,
        String displayEnglishName,
        String displayKoreanName) {}
