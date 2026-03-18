package com.ryuqq.marketplace.application.claimsync.manager;

import com.ryuqq.marketplace.application.claimsync.port.out.command.ClaimSyncLogCommandPort;
import com.ryuqq.marketplace.domain.claimsync.aggregate.ClaimSyncLog;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 클레임 동기화 로그 저장 Manager. */
@Component
public class ClaimSyncLogCommandManager {

    private final ClaimSyncLogCommandPort commandPort;

    public ClaimSyncLogCommandManager(ClaimSyncLogCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * 클레임 동기화 로그를 영속화합니다.
     *
     * @param syncLog 저장할 동기화 로그
     */
    @Transactional
    public void record(ClaimSyncLog syncLog) {
        commandPort.persist(syncLog);
    }
}
