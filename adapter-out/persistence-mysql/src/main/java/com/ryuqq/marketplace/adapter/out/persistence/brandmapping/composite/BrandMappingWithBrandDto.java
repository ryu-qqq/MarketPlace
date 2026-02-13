package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.composite;

/** 브랜드 매핑 + 내부 브랜드 JOIN DTO. */
public record BrandMappingWithBrandDto(
        Long brandMappingId, Long internalBrandId, String brandName) {}
