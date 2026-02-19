package com.ryuqq.marketplace.application.externalbrandmapping.dto.response;

import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import java.time.Instant;

/** 외부 브랜드 매핑 조회 결과 DTO. */
public record ExternalBrandMappingResult(
        Long id,
        Long externalSourceId,
        String externalBrandCode,
        String externalBrandName,
        Long internalBrandId,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static ExternalBrandMappingResult from(ExternalBrandMapping mapping) {
        return new ExternalBrandMappingResult(
                mapping.idValue(),
                mapping.externalSourceId(),
                mapping.externalBrandCode(),
                mapping.externalBrandName(),
                mapping.internalBrandId(),
                mapping.status().name(),
                mapping.createdAt(),
                mapping.updatedAt());
    }
}
