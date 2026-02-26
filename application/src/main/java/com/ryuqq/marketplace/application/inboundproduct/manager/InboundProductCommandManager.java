package com.ryuqq.marketplace.application.inboundproduct.manager;

import com.ryuqq.marketplace.application.inboundproduct.port.out.command.InboundProductCommandPort;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InboundProductCommandManager {

    private final InboundProductCommandPort commandPort;

    public InboundProductCommandManager(InboundProductCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(InboundProduct product) {
        return commandPort.persist(product);
    }

    @Transactional
    public List<Long> persistAll(List<InboundProduct> products) {
        return commandPort.persistAll(products);
    }
}
