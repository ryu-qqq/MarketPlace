package com.ryuqq.marketplace.application.categorypreset.manager;

import com.ryuqq.marketplace.application.categorypreset.port.out.command.CategoryPresetCommandPort;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** CategoryPreset Command Manager. */
@Component
public class CategoryPresetCommandManager {

    private final CategoryPresetCommandPort commandPort;

    public CategoryPresetCommandManager(CategoryPresetCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(CategoryPreset categoryPreset) {
        return commandPort.persist(categoryPreset);
    }

    @Transactional
    public List<Long> persistAll(List<CategoryPreset> categoryPresets) {
        return commandPort.persistAll(categoryPresets);
    }
}
