package com.ryuqq.marketplace.application.categorymapping.manager;

import com.ryuqq.marketplace.application.categorymapping.port.out.command.CategoryMappingCommandPort;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** CategoryMapping Command Manager. */
@Component
public class CategoryMappingCommandManager {

    private final CategoryMappingCommandPort commandPort;

    public CategoryMappingCommandManager(CategoryMappingCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(CategoryMapping categoryMapping) {
        return commandPort.persist(categoryMapping);
    }
}
