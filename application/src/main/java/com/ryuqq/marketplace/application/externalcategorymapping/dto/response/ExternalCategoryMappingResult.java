package com.ryuqq.marketplace.application.externalcategorymapping.dto.response;

import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import java.time.Instant;

/** 외부 카테고리 매핑 조회 결과 DTO. */
public record ExternalCategoryMappingResult(
        Long id,
        Long externalSourceId,
        String externalCategoryCode,
        String externalCategoryName,
        Long internalCategoryId,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static ExternalCategoryMappingResult from(ExternalCategoryMapping mapping) {
        return new ExternalCategoryMappingResult(
                mapping.idValue(),
                mapping.externalSourceId(),
                mapping.externalCategoryCode(),
                mapping.externalCategoryName(),
                mapping.internalCategoryId(),
                mapping.status().name(),
                mapping.createdAt(),
                mapping.updatedAt());
    }
}
