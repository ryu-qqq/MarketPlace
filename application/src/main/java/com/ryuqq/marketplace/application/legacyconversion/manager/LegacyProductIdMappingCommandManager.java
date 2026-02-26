package com.ryuqq.marketplace.application.legacyconversion.manager;

import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyProductIdMappingCommandPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/** 레거시 상품(SKU) ID 매핑 명령 Manager. */
@Component
public class LegacyProductIdMappingCommandManager {

    private final LegacyProductIdMappingCommandPort commandPort;

    public LegacyProductIdMappingCommandManager(LegacyProductIdMappingCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * ID 매핑 영속화.
     *
     * @param mapping 영속화할 매핑
     * @return 영속화된 매핑 ID
     */
    public Long persist(LegacyProductIdMapping mapping) {
        return commandPort.persist(mapping);
    }

    /**
     * 다건 ID 매핑 일괄 영속화.
     *
     * @param mappings 영속화할 매핑 목록
     */
    public void persistAll(List<LegacyProductIdMapping> mappings) {
        commandPort.persistAll(mappings);
    }
}
