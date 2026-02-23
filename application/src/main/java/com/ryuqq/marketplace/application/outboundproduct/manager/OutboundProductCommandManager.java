package com.ryuqq.marketplace.application.outboundproduct.manager;

import com.ryuqq.marketplace.application.outboundproduct.port.out.command.OutboundProductCommandPort;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboundProductCommandManager {

    private final OutboundProductCommandPort commandPort;

    public OutboundProductCommandManager(OutboundProductCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(OutboundProduct product) {
        return commandPort.persist(product);
    }

    @Transactional
    public List<Long> persistAll(List<OutboundProduct> products) {
        if (products.isEmpty()) {
            return List.of();
        }
        return commandPort.persistAll(products);
    }
}
