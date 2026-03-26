package com.ryuqq.marketplace.application.claim.manager;

import com.ryuqq.marketplace.application.claim.port.out.command.ClaimShipmentCommandPort;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ClaimShipment Write Manager. */
@Component
public class ClaimShipmentCommandManager {

    private final ClaimShipmentCommandPort commandPort;

    public ClaimShipmentCommandManager(ClaimShipmentCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(ClaimShipment claimShipment) {
        commandPort.persist(claimShipment);
    }
}
