package com.ryuqq.marketplace.application.inboundorder.manager;

import com.ryuqq.marketplace.application.inboundorder.port.out.command.InboundOrderCommandPort;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** InboundOrder 저장 Manager. */
@Component
public class InboundOrderCommandManager {

    private final InboundOrderCommandPort commandPort;

    public InboundOrderCommandManager(InboundOrderCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(InboundOrder inboundOrder) {
        commandPort.save(inboundOrder);
    }
}
