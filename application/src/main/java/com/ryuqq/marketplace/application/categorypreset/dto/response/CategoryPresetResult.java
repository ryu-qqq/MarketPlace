package com.ryuqq.marketplace.application.categorypreset.dto.response;

import java.time.Instant;

/** 카테고리 프리셋 조회 결과 DTO. */
public record CategoryPresetResult(
        Long id,
        Long shopId,
        String shopName,
        Long salesChannelId,
        String salesChannelName,
        String accountId,
        String presetName,
        String categoryPath,
        String categoryCode,
        Instant createdAt) {}
