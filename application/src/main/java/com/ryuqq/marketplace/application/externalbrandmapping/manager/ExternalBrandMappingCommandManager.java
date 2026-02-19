package com.ryuqq.marketplace.application.externalbrandmapping.manager;

import com.ryuqq.marketplace.application.externalbrandmapping.port.out.command.ExternalBrandMappingCommandPort;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ExternalBrandMapping Command Manager. */
@Component
@Transactional
public class ExternalBrandMappingCommandManager {

    private final ExternalBrandMappingCommandPort commandPort;

    public ExternalBrandMappingCommandManager(ExternalBrandMappingCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    public Long persist(ExternalBrandMapping mapping) {
        return commandPort.persist(mapping);
    }

    public List<Long> persistAll(List<ExternalBrandMapping> mappings) {
        return commandPort.persistAll(mappings);
    }
}
