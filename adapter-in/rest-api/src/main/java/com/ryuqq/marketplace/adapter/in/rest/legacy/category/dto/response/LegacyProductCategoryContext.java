package com.ryuqq.marketplace.adapter.in.rest.legacy.category.dto.response;

/** 세토프 ProductCategoryContext 호환 응답 DTO. */
public record LegacyProductCategoryContext(
        long categoryId,
        String categoryName,
        String displayName,
        int categoryDepth,
        String categoryFullPath,
        String targetGroup) {}
