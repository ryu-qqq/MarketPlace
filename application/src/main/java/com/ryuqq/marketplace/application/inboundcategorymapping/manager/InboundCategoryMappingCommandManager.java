package com.ryuqq.marketplace.application.inboundcategorymapping.manager;

import com.ryuqq.marketplace.application.inboundcategorymapping.port.out.command.InboundCategoryMappingCommandPort;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** InboundCategoryMapping Command Manager. */
@Component
public class InboundCategoryMappingCommandManager {

    private final InboundCategoryMappingCommandPort commandPort;

    public InboundCategoryMappingCommandManager(InboundCategoryMappingCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(InboundCategoryMapping mapping) {
        return commandPort.persist(mapping);
    }

    @Transactional
    public List<Long> persistAll(List<InboundCategoryMapping> mappings) {
        return commandPort.persistAll(mappings);
    }
}
