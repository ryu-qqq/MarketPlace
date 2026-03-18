package com.ryuqq.marketplace.application.claimsync.port.out.command;

import com.ryuqq.marketplace.domain.claimsync.aggregate.ClaimSyncLog;

/** 클레임 동기화 로그 저장 포트. */
public interface ClaimSyncLogCommandPort {

    void persist(ClaimSyncLog syncLog);
}
