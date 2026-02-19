package com.ryuqq.marketplace.application.externalcategorymapping.manager;

import com.ryuqq.marketplace.application.externalcategorymapping.port.out.command.ExternalCategoryMappingCommandPort;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ExternalCategoryMapping Command Manager. */
@Component
public class ExternalCategoryMappingCommandManager {

    private final ExternalCategoryMappingCommandPort commandPort;

    public ExternalCategoryMappingCommandManager(ExternalCategoryMappingCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ExternalCategoryMapping mapping) {
        return commandPort.persist(mapping);
    }

    @Transactional
    public List<Long> persistAll(List<ExternalCategoryMapping> mappings) {
        return commandPort.persistAll(mappings);
    }
}
