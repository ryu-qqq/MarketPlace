package com.ryuqq.marketplace.application.exchange.manager;

import com.ryuqq.marketplace.application.exchange.port.out.command.ExchangeCommandPort;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ExchangeClaim Write Manager. */
@Component
public class ExchangeCommandManager {

    private final ExchangeCommandPort commandPort;

    public ExchangeCommandManager(ExchangeCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(ExchangeClaim claim) {
        commandPort.persist(claim);
    }

    @Transactional
    public void persistAll(List<ExchangeClaim> claims) {
        claims.forEach(commandPort::persist);
    }
}
