package com.ryuqq.marketplace.application.brandpreset.port.out.command;

import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import java.util.List;

/** BrandPreset Command Port. */
public interface BrandPresetCommandPort {
    Long persist(BrandPreset brandPreset);

    List<Long> persistAll(List<BrandPreset> brandPresets);
}
