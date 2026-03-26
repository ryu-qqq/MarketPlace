package com.ryuqq.marketplace.application.legacyconversion.manager;

import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyOrderIdMappingCommandPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 주문 ID 매핑 명령 Manager. */
@Component
public class LegacyOrderIdMappingCommandManager {

    private final LegacyOrderIdMappingCommandPort commandPort;

    public LegacyOrderIdMappingCommandManager(LegacyOrderIdMappingCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * ID 매핑 영속화.
     *
     * @param mapping 영속화할 매핑
     * @return 영속화된 매핑 ID
     */
    @Transactional
    public Long persist(LegacyOrderIdMapping mapping) {
        return commandPort.persist(mapping);
    }

    /**
     * 다건 ID 매핑 일괄 영속화.
     *
     * @param mappings 영속화할 매핑 목록
     * @return 영속화된 매핑 ID 목록
     */
    @Transactional
    public List<Long> persistAll(List<LegacyOrderIdMapping> mappings) {
        return commandPort.persistAll(mappings);
    }
}
