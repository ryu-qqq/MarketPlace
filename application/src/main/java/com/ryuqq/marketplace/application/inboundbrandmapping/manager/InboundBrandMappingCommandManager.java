package com.ryuqq.marketplace.application.inboundbrandmapping.manager;

import com.ryuqq.marketplace.application.inboundbrandmapping.port.out.command.InboundBrandMappingCommandPort;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** InboundBrandMapping Command Manager. */
@Component
public class InboundBrandMappingCommandManager {

    private final InboundBrandMappingCommandPort commandPort;

    public InboundBrandMappingCommandManager(InboundBrandMappingCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(InboundBrandMapping mapping) {
        return commandPort.persist(mapping);
    }

    @Transactional
    public List<Long> persistAll(List<InboundBrandMapping> mappings) {
        return commandPort.persistAll(mappings);
    }
}
