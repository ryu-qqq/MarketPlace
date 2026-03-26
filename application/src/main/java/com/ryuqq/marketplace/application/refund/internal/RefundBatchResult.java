package com.ryuqq.marketplace.application.refund.internal;

import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.util.ArrayList;
import java.util.List;

/**
 * 환불 배치 처리 결과 수집기.
 *
 * <p>RefundClaim + RefundOutbox + ClaimHistory + BatchItemResult를 하나로 묶어 관리합니다.
 */
public class RefundBatchResult {

    private final String operationName;
    private final List<RefundClaim> claims;
    private final List<RefundOutbox> outboxes;
    private final List<ClaimHistory> histories;
    private final List<BatchItemResult<String>> results;

    private RefundBatchResult(String operationName) {
        this.operationName = operationName;
        this.claims = new ArrayList<>();
        this.outboxes = new ArrayList<>();
        this.histories = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    public static RefundBatchResult create(String operationName) {
        return new RefundBatchResult(operationName);
    }

    public void addSuccess(RefundClaim claim, RefundOutbox outbox, ClaimHistory history) {
        claims.add(claim);
        outboxes.add(outbox);
        histories.add(history);
        results.add(BatchItemResult.success(claim.idValue()));
    }

    public void addFailure(String id, String message) {
        results.add(BatchItemResult.failure(id, operationName + "_FAILED", message));
    }

    public boolean hasSuccessItems() {
        return !claims.isEmpty();
    }

    public List<RefundClaim> claims() {
        return claims;
    }

    public List<RefundOutbox> outboxes() {
        return outboxes;
    }

    public List<ClaimHistory> histories() {
        return histories;
    }

    public BatchProcessingResult<String> toBatchProcessingResult() {
        return BatchProcessingResult.from(results);
    }
}
