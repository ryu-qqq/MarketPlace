package com.ryuqq.marketplace.application.cancel.dto;

import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import java.util.ArrayList;
import java.util.List;

/**
 * 취소 배치 처리 결과 수집기.
 *
 * <p>Cancel + CancelOutbox + ClaimHistory + BatchItemResult를 하나로 묶어 관리합니다. Service에서 배치 처리 시 결과를
 * 수집하고, 최종적으로 persist + BatchProcessingResult 변환에 사용합니다.
 */
public class CancelBatchResult {

    private final List<Cancel> cancels;
    private final List<CancelOutbox> outboxes;
    private final List<ClaimHistory> histories;
    private final List<BatchItemResult<String>> results;

    private CancelBatchResult() {
        this.cancels = new ArrayList<>();
        this.outboxes = new ArrayList<>();
        this.histories = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    public static CancelBatchResult create() {
        return new CancelBatchResult();
    }

    public void addSuccess(Cancel cancel, CancelOutbox outbox, ClaimHistory history) {
        cancels.add(cancel);
        outboxes.add(outbox);
        histories.add(history);
        results.add(BatchItemResult.success(cancel.idValue()));
    }

    public void addFailure(String id, String errorCode, String message) {
        results.add(BatchItemResult.failure(id, errorCode, message));
    }

    public boolean hasSuccessItems() {
        return !cancels.isEmpty();
    }

    public List<Cancel> cancels() {
        return cancels;
    }

    public List<CancelOutbox> outboxes() {
        return outboxes;
    }

    public List<ClaimHistory> histories() {
        return histories;
    }

    public BatchProcessingResult<String> toBatchProcessingResult() {
        return BatchProcessingResult.from(results);
    }
}
