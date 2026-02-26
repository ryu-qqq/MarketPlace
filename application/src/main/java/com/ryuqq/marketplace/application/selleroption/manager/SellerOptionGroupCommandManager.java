package com.ryuqq.marketplace.application.selleroption.manager;

import com.ryuqq.marketplace.application.selleroption.port.out.command.SellerOptionGroupCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SellerOptionGroup Command Manager. */
@Component
public class SellerOptionGroupCommandManager {

    private final SellerOptionGroupCommandPort commandPort;

    public SellerOptionGroupCommandManager(SellerOptionGroupCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(SellerOptionGroup group) {
        return commandPort.persist(group);
    }

    @Transactional
    public void persistAll(List<SellerOptionGroup> groups) {
        commandPort.persistAll(groups);
    }
}
