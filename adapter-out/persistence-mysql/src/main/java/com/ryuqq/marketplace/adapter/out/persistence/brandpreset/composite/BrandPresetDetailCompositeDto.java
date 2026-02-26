package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.composite;

import java.time.Instant;

/** 브랜드 프리셋 상세 복합 조회 DTO (JOIN 결과). */
public record BrandPresetDetailCompositeDto(
        Long id,
        Long shopId,
        String shopName,
        String accountId,
        Long salesChannelId,
        String salesChannelName,
        Long salesChannelBrandId,
        String externalBrandCode,
        String externalBrandName,
        String presetName,
        String status,
        Instant createdAt,
        Instant updatedAt) {}
