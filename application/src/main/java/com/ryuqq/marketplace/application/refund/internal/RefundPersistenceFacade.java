package com.ryuqq.marketplace.application.refund.internal;

import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxCommandManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 환불 퍼시스트 파사드.
 *
 * <p>RefundClaim + RefundOutbox + ClaimHistory + OrderItem 상태 변경을 같은 트랜잭션에서 일괄 처리합니다.
 */
@Component
public class RefundPersistenceFacade {

    private final RefundCommandManager refundCommandManager;
    private final RefundOutboxCommandManager outboxCommandManager;
    private final ClaimHistoryCommandManager historyCommandManager;
    private final OrderItemCommandManager orderItemCommandManager;

    public RefundPersistenceFacade(
            RefundCommandManager refundCommandManager,
            RefundOutboxCommandManager outboxCommandManager,
            ClaimHistoryCommandManager historyCommandManager,
            OrderItemCommandManager orderItemCommandManager) {
        this.refundCommandManager = refundCommandManager;
        this.outboxCommandManager = outboxCommandManager;
        this.historyCommandManager = historyCommandManager;
        this.orderItemCommandManager = orderItemCommandManager;
    }

    /** Bundle 내 모든 도메인 객체를 같은 트랜잭션에서 일괄 저장합니다. */
    @Transactional
    public void persistAll(RefundPersistenceBundle bundle) {
        refundCommandManager.persistAll(bundle.claims());
        outboxCommandManager.persistAll(bundle.outboxes());
        if (!bundle.histories().isEmpty()) {
            historyCommandManager.persistAll(bundle.histories());
        }
        if (!bundle.orderItems().isEmpty()) {
            orderItemCommandManager.persistAll(bundle.orderItems());
        }
    }
}
