package com.ryuqq.marketplace.application.brandpreset.dto.response;

import java.time.Instant;
import java.util.List;

/** 브랜드 프리셋 상세 조회 결과 DTO. */
public record BrandPresetDetailResult(
        Long id,
        Long shopId,
        String shopName,
        Long salesChannelId,
        String salesChannelName,
        String accountId,
        String presetName,
        MappingBrand mappingBrand,
        List<InternalBrand> internalBrands,
        Instant createdAt,
        Instant updatedAt) {

    public record MappingBrand(String brandCode, String brandName) {}

    public record InternalBrand(Long id, String brandName) {}
}
