package com.ryuqq.marketplace.application.categorymapping.port.out.command;

import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import java.util.List;

/** CategoryMapping Command Port. */
public interface CategoryMappingCommandPort {
    Long persist(CategoryMapping categoryMapping);

    List<Long> persistAll(List<CategoryMapping> categoryMappings);

    void deleteAllByPresetId(Long presetId);

    void deleteAllByPresetIds(List<Long> presetIds);
}
