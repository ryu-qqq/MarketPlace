package com.ryuqq.marketplace.application.shop.manager;

import com.ryuqq.marketplace.application.shop.port.out.command.ShopCommandPort;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Shop Write Manager. */
@Component
public class ShopWriteManager {

    private final ShopCommandPort commandPort;

    public ShopWriteManager(ShopCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(Shop shop) {
        return commandPort.persist(shop);
    }
}
