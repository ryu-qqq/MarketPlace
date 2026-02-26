package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.composite;

import java.time.Instant;

/** 브랜드 프리셋 조인 조회 결과 DTO. */
public record BrandPresetCompositeDto(
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
        Instant createdAt) {}
