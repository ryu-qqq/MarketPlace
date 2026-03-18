package com.ryuqq.marketplace.application.cancel.internal;

import com.ryuqq.marketplace.application.cancel.manager.CancelCommandManager;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxCommandManager;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 취소 퍼시스트 파사드.
 *
 * <p>Cancel + CancelOutbox + OrderItem 상태 변경을 같은 트랜잭션에서 일괄 처리합니다.
 */
@Component
public class CancelPersistenceFacade {

    private final CancelCommandManager cancelCommandManager;
    private final CancelOutboxCommandManager outboxCommandManager;
    private final ClaimHistoryCommandManager historyCommandManager;
    private final OrderItemCommandManager orderItemCommandManager;

    public CancelPersistenceFacade(
            CancelCommandManager cancelCommandManager,
            CancelOutboxCommandManager outboxCommandManager,
            ClaimHistoryCommandManager historyCommandManager,
            OrderItemCommandManager orderItemCommandManager) {
        this.cancelCommandManager = cancelCommandManager;
        this.outboxCommandManager = outboxCommandManager;
        this.historyCommandManager = historyCommandManager;
        this.orderItemCommandManager = orderItemCommandManager;
    }

    /** Cancel + Outbox 단건 저장. */
    @Transactional
    public void persistWithOutbox(Cancel cancel, CancelOutbox outbox) {
        cancelCommandManager.persist(cancel);
        outboxCommandManager.persist(outbox);
    }

    /** Cancel + Outbox 일괄 저장. */
    @Transactional
    public void persistAllWithOutboxes(List<Cancel> cancels, List<CancelOutbox> outboxes) {
        cancelCommandManager.persistAll(cancels);
        outboxCommandManager.persistAll(outboxes);
    }

    /** Cancel + Outbox + History 일괄 저장 (신규 생성 시). */
    @Transactional
    public void persistAllWithOutboxesAndHistories(
            List<Cancel> cancels,
            List<CancelOutbox> outboxes,
            List<ClaimHistory> histories) {
        cancelCommandManager.persistAll(cancels);
        outboxCommandManager.persistAll(outboxes);
        historyCommandManager.persistAll(histories);
    }

    /** Cancel + Outbox + History + OrderItem 상태 변경 일괄 저장 (신규 생성 + OrderItem 취소). */
    @Transactional
    public void persistAllWithOutboxesAndHistoriesAndOrderItems(
            List<Cancel> cancels,
            List<CancelOutbox> outboxes,
            List<ClaimHistory> histories,
            List<OrderItem> orderItems) {
        cancelCommandManager.persistAll(cancels);
        outboxCommandManager.persistAll(outboxes);
        historyCommandManager.persistAll(histories);
        orderItemCommandManager.persistAll(orderItems);
    }

    /** Cancel만 일괄 저장 + Outbox 일괄 저장 (승인/거절 시). */
    @Transactional
    public void persistCancelsWithOutboxes(List<Cancel> cancels, List<CancelOutbox> outboxes) {
        cancelCommandManager.persistAll(cancels);
        outboxCommandManager.persistAll(outboxes);
    }

    /** Cancel + Outbox + History 일괄 저장 (승인/거절 시). */
    @Transactional
    public void persistCancelsWithOutboxesAndHistories(
            List<Cancel> cancels,
            List<CancelOutbox> outboxes,
            List<ClaimHistory> histories) {
        cancelCommandManager.persistAll(cancels);
        outboxCommandManager.persistAll(outboxes);
        historyCommandManager.persistAll(histories);
    }

    /** Cancel + Outbox + History + OrderItem 상태 변경 일괄 저장 (승인 시 OrderItem 취소). */
    @Transactional
    public void persistCancelsWithOutboxesAndHistoriesAndOrderItems(
            List<Cancel> cancels,
            List<CancelOutbox> outboxes,
            List<ClaimHistory> histories,
            List<OrderItem> orderItems) {
        cancelCommandManager.persistAll(cancels);
        outboxCommandManager.persistAll(outboxes);
        historyCommandManager.persistAll(histories);
        orderItemCommandManager.persistAll(orderItems);
    }
}
