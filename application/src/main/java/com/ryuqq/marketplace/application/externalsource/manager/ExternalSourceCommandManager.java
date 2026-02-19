package com.ryuqq.marketplace.application.externalsource.manager;

import com.ryuqq.marketplace.application.externalsource.port.out.command.ExternalSourceCommandPort;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ExternalSource Command Manager. */
@Component
@Transactional
public class ExternalSourceCommandManager {

    private final ExternalSourceCommandPort commandPort;

    public ExternalSourceCommandManager(ExternalSourceCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    public Long persist(ExternalSource externalSource) {
        return commandPort.persist(externalSource);
    }

    public boolean existsByCode(String code) {
        return commandPort.existsByCode(code);
    }
}
