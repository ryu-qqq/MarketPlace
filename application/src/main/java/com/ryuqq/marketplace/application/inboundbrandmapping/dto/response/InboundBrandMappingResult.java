package com.ryuqq.marketplace.application.inboundbrandmapping.dto.response;

import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import java.time.Instant;

/** 외부 브랜드 매핑 조회 결과 DTO. */
public record InboundBrandMappingResult(
        Long id,
        Long inboundSourceId,
        String externalBrandCode,
        String externalBrandName,
        Long internalBrandId,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static InboundBrandMappingResult from(InboundBrandMapping mapping) {
        return new InboundBrandMappingResult(
                mapping.idValue(),
                mapping.inboundSourceId(),
                mapping.externalBrandCode(),
                mapping.externalBrandName(),
                mapping.internalBrandId(),
                mapping.status().name(),
                mapping.createdAt(),
                mapping.updatedAt());
    }
}
