package com.ryuqq.marketplace.application.saleschannel.manager;

import com.ryuqq.marketplace.application.saleschannel.port.out.command.SalesChannelCommandPort;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SalesChannel Write Manager. */
@Component
public class SalesChannelCommandManager {

    private final SalesChannelCommandPort commandPort;

    public SalesChannelCommandManager(SalesChannelCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(SalesChannel salesChannel) {
        return commandPort.persist(salesChannel);
    }
}
