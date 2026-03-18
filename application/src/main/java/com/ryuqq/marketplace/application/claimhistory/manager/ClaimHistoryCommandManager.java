package com.ryuqq.marketplace.application.claimhistory.manager;

import com.ryuqq.marketplace.application.claimhistory.port.out.command.ClaimHistoryCommandPort;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 클레임 이력 Write Manager. */
@Component
public class ClaimHistoryCommandManager {

    private final ClaimHistoryCommandPort commandPort;

    public ClaimHistoryCommandManager(ClaimHistoryCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(ClaimHistory history) {
        commandPort.persist(history);
    }

    @Transactional
    public void persistAll(List<ClaimHistory> histories) {
        commandPort.persistAll(histories);
    }
}
