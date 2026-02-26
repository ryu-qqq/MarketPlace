package com.ryuqq.marketplace.application.saleschannelcategory.manager;

import com.ryuqq.marketplace.application.saleschannelcategory.port.out.command.SalesChannelCategoryCommandPort;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SalesChannelCategory Write Manager. */
@Component
public class SalesChannelCategoryCommandManager {

    private final SalesChannelCategoryCommandPort commandPort;

    public SalesChannelCategoryCommandManager(SalesChannelCategoryCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(SalesChannelCategory salesChannelCategory) {
        return commandPort.persist(salesChannelCategory);
    }
}
