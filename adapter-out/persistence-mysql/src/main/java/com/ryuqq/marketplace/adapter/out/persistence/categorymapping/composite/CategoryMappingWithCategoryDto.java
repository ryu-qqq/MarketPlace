package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.composite;

/** 카테고리 매핑 + 내부 카테고리 JOIN DTO. */
public record CategoryMappingWithCategoryDto(
        Long categoryMappingId,
        Long internalCategoryId,
        String categoryName,
        String displayPath,
        String code) {}
