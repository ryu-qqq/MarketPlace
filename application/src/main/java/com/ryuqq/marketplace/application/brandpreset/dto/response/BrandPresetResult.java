package com.ryuqq.marketplace.application.brandpreset.dto.response;

import java.time.Instant;

/** 브랜드 프리셋 조회 결과 DTO. */
public record BrandPresetResult(
        Long id,
        Long shopId,
        String shopName,
        Long salesChannelId,
        String salesChannelName,
        String accountId,
        String presetName,
        String brandName,
        String brandCode,
        Instant createdAt) {}
