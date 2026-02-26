package com.ryuqq.marketplace.application.categorypreset.dto.bundle;

import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import java.time.Instant;
import java.util.List;

/** 카테고리 프리셋 수정 시 Preset 수정 + Mapping 교체에 필요한 데이터 번들. */
public record UpdateCategoryPresetBundle(
        CategoryPreset categoryPreset,
        String presetName,
        Long salesChannelCategoryId,
        List<CategoryMapping> categoryMappings,
        Instant now) {}
