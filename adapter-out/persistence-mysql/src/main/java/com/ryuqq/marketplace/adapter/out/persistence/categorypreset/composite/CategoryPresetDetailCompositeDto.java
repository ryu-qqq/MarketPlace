package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.composite;

import java.time.Instant;

/** 카테고리 프리셋 상세 복합 조회 DTO (JOIN 결과). */
public record CategoryPresetDetailCompositeDto(
        Long id,
        Long shopId,
        String shopName,
        String accountId,
        Long salesChannelId,
        String salesChannelName,
        Long salesChannelCategoryId,
        String externalCategoryCode,
        String categoryDisplayPath,
        String presetName,
        String status,
        Instant createdAt,
        Instant updatedAt) {}
