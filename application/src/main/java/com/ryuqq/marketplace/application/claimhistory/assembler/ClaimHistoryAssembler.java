package com.ryuqq.marketplace.application.claimhistory.assembler;

import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import java.util.List;
import org.springframework.stereotype.Component;

/** 클레임 이력 도메인 → Response DTO 변환. */
@Component
public class ClaimHistoryAssembler {

    public ClaimHistoryResult toResult(ClaimHistory history) {
        return new ClaimHistoryResult(
                history.idValue(),
                history.historyType().name(),
                history.title(),
                history.message(),
                history.actor().actorType().name(),
                history.actor().actorId(),
                history.actor().actorName(),
                history.createdAt());
    }

    public List<ClaimHistoryResult> toResults(List<ClaimHistory> histories) {
        return histories.stream().map(this::toResult).toList();
    }
}
