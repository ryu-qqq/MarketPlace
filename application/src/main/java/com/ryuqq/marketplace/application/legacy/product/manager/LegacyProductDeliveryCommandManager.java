package com.ryuqq.marketplace.application.legacy.product.manager;

import com.ryuqq.marketplace.application.legacy.product.port.out.command.LegacyProductDeliveryCommandPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productdelivery.aggregate.LegacyProductDelivery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 배송정보 Command Manager. */
@Component
public class LegacyProductDeliveryCommandManager {

    private final LegacyProductDeliveryCommandPort commandPort;

    public LegacyProductDeliveryCommandManager(LegacyProductDeliveryCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(LegacyProductGroupId productGroupId, LegacyProductDelivery delivery) {
        commandPort.persist(productGroupId, delivery);
    }
}
