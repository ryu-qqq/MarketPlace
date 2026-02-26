package com.ryuqq.marketplace.application.brandmapping.manager;

import com.ryuqq.marketplace.application.brandmapping.port.out.command.BrandMappingCommandPort;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** BrandMapping Command Manager. */
@Component
public class BrandMappingCommandManager {

    private final BrandMappingCommandPort commandPort;

    public BrandMappingCommandManager(BrandMappingCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(BrandMapping brandMapping) {
        return commandPort.persist(brandMapping);
    }

    @Transactional
    public List<Long> persistAll(List<BrandMapping> brandMappings) {
        return commandPort.persistAll(brandMappings);
    }

    @Transactional
    public void deleteAllByPresetId(Long presetId) {
        commandPort.deleteAllByPresetId(presetId);
    }

    @Transactional
    public void deleteAllByPresetIds(List<Long> presetIds) {
        commandPort.deleteAllByPresetIds(presetIds);
    }
}
