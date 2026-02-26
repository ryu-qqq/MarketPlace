package com.ryuqq.marketplace.application.brandpreset.dto.bundle;

import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import java.time.Instant;
import java.util.List;

/** 브랜드 프리셋 등록 시 Preset + Mapping 생성에 필요한 데이터 번들. */
public record RegisterBrandPresetBundle(
        BrandPreset brandPreset,
        Long salesChannelBrandId,
        List<Long> internalBrandIds,
        Instant now) {

    /** persist 후 반환된 presetId로 BrandMapping 목록 생성. */
    public List<BrandMapping> createMappings(Long presetId) {
        if (internalBrandIds == null || internalBrandIds.isEmpty()) {
            return List.of();
        }
        return internalBrandIds.stream()
                .map(id -> BrandMapping.forNew(presetId, salesChannelBrandId, id, now))
                .toList();
    }
}
