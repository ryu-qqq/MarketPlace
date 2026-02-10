package com.ryuqq.marketplace.application.categorypreset.port.out.command;

import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import java.util.List;

/** CategoryPreset Command Port. */
public interface CategoryPresetCommandPort {
    Long persist(CategoryPreset categoryPreset);

    List<Long> persistAll(List<CategoryPreset> categoryPresets);
}
