package com.ryuqq.marketplace.application.brandmapping.dto.response;

import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import java.time.Instant;

/** 브랜드 매핑 조회 결과 DTO. */
public record BrandMappingResult(
        Long id,
        Long salesChannelBrandId,
        Long internalBrandId,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static BrandMappingResult from(BrandMapping brandMapping) {
        return new BrandMappingResult(
                brandMapping.idValue(),
                brandMapping.salesChannelBrandId(),
                brandMapping.internalBrandId(),
                brandMapping.status().name(),
                brandMapping.createdAt(),
                brandMapping.updatedAt());
    }
}
