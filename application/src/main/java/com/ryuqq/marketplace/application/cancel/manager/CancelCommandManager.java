package com.ryuqq.marketplace.application.cancel.manager;

import com.ryuqq.marketplace.application.cancel.port.out.command.CancelCommandPort;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Cancel Write Manager. */
@Component
public class CancelCommandManager {

    private final CancelCommandPort commandPort;

    public CancelCommandManager(CancelCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(Cancel cancel) {
        commandPort.persist(cancel);
    }

    @Transactional
    public void persistAll(List<Cancel> cancels) {
        commandPort.persistAll(cancels);
    }
}
