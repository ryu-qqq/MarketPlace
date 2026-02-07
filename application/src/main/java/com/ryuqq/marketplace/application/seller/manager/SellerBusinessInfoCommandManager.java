package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.command.SellerBusinessInfoCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SellerBusinessInfo Command Manager. */
@Component
public class SellerBusinessInfoCommandManager {

    private final SellerBusinessInfoCommandPort commandPort;

    public SellerBusinessInfoCommandManager(SellerBusinessInfoCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(SellerBusinessInfo businessInfo) {
        return commandPort.persist(businessInfo);
    }

    @Transactional
    public void persistAll(List<SellerBusinessInfo> businessInfos) {
        commandPort.persistAll(businessInfos);
    }
}
