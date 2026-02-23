package com.ryuqq.marketplace.application.inboundcategorymapping.dto.response;

import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import java.time.Instant;

/** 외부 카테고리 매핑 조회 결과 DTO. */
public record InboundCategoryMappingResult(
        Long id,
        Long inboundSourceId,
        String externalCategoryCode,
        String externalCategoryName,
        Long internalCategoryId,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static InboundCategoryMappingResult from(InboundCategoryMapping mapping) {
        return new InboundCategoryMappingResult(
                mapping.idValue(),
                mapping.inboundSourceId(),
                mapping.externalCategoryCode(),
                mapping.externalCategoryName(),
                mapping.internalCategoryId(),
                mapping.status().name(),
                mapping.createdAt(),
                mapping.updatedAt());
    }
}
