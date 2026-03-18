package com.ryuqq.marketplace.application.refund.internal;

import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxCommandManager;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 환불 퍼시스트 파사드.
 *
 * <p>RefundClaim + RefundOutbox를 같은 트랜잭션에서 일괄 저장합니다.
 */
@Component
public class RefundPersistenceFacade {

    private final RefundCommandManager refundCommandManager;
    private final RefundOutboxCommandManager outboxCommandManager;
    private final ClaimHistoryCommandManager historyCommandManager;

    public RefundPersistenceFacade(
            RefundCommandManager refundCommandManager,
            RefundOutboxCommandManager outboxCommandManager,
            ClaimHistoryCommandManager historyCommandManager) {
        this.refundCommandManager = refundCommandManager;
        this.outboxCommandManager = outboxCommandManager;
        this.historyCommandManager = historyCommandManager;
    }

    @Transactional
    public void persistWithOutbox(RefundClaim claim, RefundOutbox outbox) {
        refundCommandManager.persist(claim);
        outboxCommandManager.persist(outbox);
    }

    @Transactional
    public void persistAllWithOutboxes(List<RefundClaim> claims, List<RefundOutbox> outboxes) {
        refundCommandManager.persistAll(claims);
        outboxCommandManager.persistAll(outboxes);
    }

    /** RefundClaim + Outbox + History 일괄 저장 (신규 생성 시). */
    @Transactional
    public void persistAllWithOutboxesAndHistories(
            List<RefundClaim> claims,
            List<RefundOutbox> outboxes,
            List<ClaimHistory> histories) {
        refundCommandManager.persistAll(claims);
        outboxCommandManager.persistAll(outboxes);
        historyCommandManager.persistAll(histories);
    }

    @Transactional
    public void persistClaimsWithOutboxes(List<RefundClaim> claims, List<RefundOutbox> outboxes) {
        refundCommandManager.persistAll(claims);
        outboxCommandManager.persistAll(outboxes);
    }

    /** RefundClaim + Outbox + History 일괄 저장 (승인/거절 시). */
    @Transactional
    public void persistClaimsWithOutboxesAndHistories(
            List<RefundClaim> claims,
            List<RefundOutbox> outboxes,
            List<ClaimHistory> histories) {
        refundCommandManager.persistAll(claims);
        outboxCommandManager.persistAll(outboxes);
        historyCommandManager.persistAll(histories);
    }
}
