package com.ryuqq.marketplace.application.claimhistory.port.out.command;

import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import java.util.List;

/** 클레임 이력 Command Port. */
public interface ClaimHistoryCommandPort {

    void persist(ClaimHistory history);

    void persistAll(List<ClaimHistory> histories);
}
