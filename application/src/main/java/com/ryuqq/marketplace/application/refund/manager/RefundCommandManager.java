package com.ryuqq.marketplace.application.refund.manager;

import com.ryuqq.marketplace.application.refund.port.out.command.RefundCommandPort;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** RefundClaim Write Manager. */
@Component
public class RefundCommandManager {

    private final RefundCommandPort commandPort;

    public RefundCommandManager(RefundCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(RefundClaim claim) {
        commandPort.persist(claim);
    }

    @Transactional
    public void persistAll(List<RefundClaim> claims) {
        claims.forEach(commandPort::persist);
    }
}
