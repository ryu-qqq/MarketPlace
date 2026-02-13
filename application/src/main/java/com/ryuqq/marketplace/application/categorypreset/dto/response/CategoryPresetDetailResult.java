package com.ryuqq.marketplace.application.categorypreset.dto.response;

import java.time.Instant;
import java.util.List;

/** 카테고리 프리셋 상세 조회 결과 DTO. */
public record CategoryPresetDetailResult(
        Long id,
        Long shopId,
        String shopName,
        Long salesChannelId,
        String salesChannelName,
        String accountId,
        String presetName,
        MappingCategory mappingCategory,
        List<InternalCategory> internalCategories,
        Instant createdAt,
        Instant updatedAt) {

    public record MappingCategory(String categoryCode, String categoryPath) {}

    public record InternalCategory(Long id, String categoryPath) {}
}
