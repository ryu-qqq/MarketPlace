package com.ryuqq.marketplace.application.brandpreset.dto.bundle;

import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import java.time.Instant;
import java.util.List;

/** 브랜드 프리셋 수정 시 Preset 수정 + Mapping 교체에 필요한 데이터 번들. */
public record UpdateBrandPresetBundle(
        BrandPreset brandPreset,
        String presetName,
        Long salesChannelBrandId,
        List<BrandMapping> brandMappings,
        Instant now) {}
