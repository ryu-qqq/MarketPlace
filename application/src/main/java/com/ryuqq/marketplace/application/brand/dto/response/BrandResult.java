package com.ryuqq.marketplace.application.brand.dto.response;

import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import java.time.Instant;

/** 브랜드 조회 결과 DTO. */
public record BrandResult(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        String shortName,
        String status,
        String logoUrl,
        Instant createdAt,
        Instant updatedAt) {

    public static BrandResult from(Brand brand) {
        return new BrandResult(
                brand.idValue(),
                brand.codeValue(),
                brand.nameKo(),
                brand.nameEn(),
                brand.shortName(),
                brand.status().name(),
                brand.logoUrlValue(),
                brand.createdAt(),
                brand.updatedAt());
    }
}
