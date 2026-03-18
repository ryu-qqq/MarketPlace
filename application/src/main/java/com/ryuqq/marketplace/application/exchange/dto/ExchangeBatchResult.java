package com.ryuqq.marketplace.application.exchange.dto;

import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.util.ArrayList;
import java.util.List;

/**
 * 교환 배치 처리 결과 수집기.
 *
 * <p>ExchangeClaim + ExchangeOutbox + ClaimHistory + BatchItemResult를 하나로 묶어 관리합니다.
 * Outbox는 nullable로 관리됩니다 (일부 액션은 외부 API 호출 불필요).
 */
public class ExchangeBatchResult {

    private final String operationName;
    private final List<ExchangeClaim> claims;
    private final List<ExchangeOutbox> outboxes;
    private final List<ClaimHistory> histories;
    private final List<BatchItemResult<String>> results;

    private ExchangeBatchResult(String operationName) {
        this.operationName = operationName;
        this.claims = new ArrayList<>();
        this.outboxes = new ArrayList<>();
        this.histories = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    public static ExchangeBatchResult create(String operationName) {
        return new ExchangeBatchResult(operationName);
    }

    /** Outbox를 포함한 성공 결과 추가. */
    public void addSuccess(ExchangeClaim claim, ExchangeOutbox outbox, ClaimHistory history) {
        claims.add(claim);
        if (outbox != null) {
            outboxes.add(outbox);
        }
        histories.add(history);
        results.add(BatchItemResult.success(claim.idValue()));
    }

    /** Outbox 없는 성공 결과 추가 (네이버 API 호출 불필요한 액션용). */
    public void addSuccess(ExchangeClaim claim, ClaimHistory history) {
        addSuccess(claim, null, history);
    }

    public void addFailure(String id, String message) {
        results.add(BatchItemResult.failure(id, operationName + "_FAILED", message));
    }

    public boolean hasSuccessItems() {
        return !claims.isEmpty();
    }

    public List<ExchangeClaim> claims() {
        return claims;
    }

    public List<ExchangeOutbox> outboxes() {
        return outboxes;
    }

    public List<ClaimHistory> histories() {
        return histories;
    }

    public BatchProcessingResult<String> toBatchProcessingResult() {
        return BatchProcessingResult.from(results);
    }
}
