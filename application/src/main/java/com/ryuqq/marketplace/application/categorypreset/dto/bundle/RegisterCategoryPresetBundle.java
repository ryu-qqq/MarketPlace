package com.ryuqq.marketplace.application.categorypreset.dto.bundle;

import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import java.time.Instant;
import java.util.List;

/** 카테고리 프리셋 등록 시 Preset + Mapping 생성에 필요한 데이터 번들. */
public record RegisterCategoryPresetBundle(
        CategoryPreset categoryPreset,
        Long salesChannelCategoryId,
        List<Long> internalCategoryIds,
        Instant now) {

    /** persist 후 반환된 presetId로 CategoryMapping 목록 생성. */
    public List<CategoryMapping> createMappings(Long presetId) {
        if (internalCategoryIds == null || internalCategoryIds.isEmpty()) {
            return List.of();
        }
        return internalCategoryIds.stream()
                .map(id -> CategoryMapping.forNew(presetId, salesChannelCategoryId, id, now))
                .toList();
    }
}
