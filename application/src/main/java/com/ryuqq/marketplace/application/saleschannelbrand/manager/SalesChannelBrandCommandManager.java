package com.ryuqq.marketplace.application.saleschannelbrand.manager;

import com.ryuqq.marketplace.application.saleschannelbrand.port.out.command.SalesChannelBrandCommandPort;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SalesChannelBrand Write Manager. */
@Component
@Transactional
public class SalesChannelBrandCommandManager {

    private final SalesChannelBrandCommandPort commandPort;

    public SalesChannelBrandCommandManager(SalesChannelBrandCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    public Long persist(SalesChannelBrand brand) {
        return commandPort.persist(brand);
    }
}
