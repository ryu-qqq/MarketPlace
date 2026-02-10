package com.ryuqq.marketplace.application.categorymapping.dto.response;

import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import java.time.Instant;

/** 카테고리 매핑 조회 결과 DTO. */
public record CategoryMappingResult(
        Long id,
        Long salesChannelCategoryId,
        Long internalCategoryId,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static CategoryMappingResult from(CategoryMapping categoryMapping) {
        return new CategoryMappingResult(
                categoryMapping.idValue(),
                categoryMapping.salesChannelCategoryId(),
                categoryMapping.internalCategoryId(),
                categoryMapping.status().name(),
                categoryMapping.createdAt(),
                categoryMapping.updatedAt());
    }
}
