package com.ryuqq.marketplace.application.exchange.internal;

import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 교환 퍼시스트 파사드.
 *
 * <p>ExchangeClaim + ExchangeOutbox + ClaimHistory + OrderItem 상태 변경을 같은 트랜잭션에서 일괄 처리합니다.
 */
@Component
public class ExchangePersistenceFacade {

    private final ExchangeCommandManager exchangeCommandManager;
    private final ExchangeOutboxCommandManager outboxCommandManager;
    private final ClaimHistoryCommandManager historyCommandManager;
    private final OrderItemCommandManager orderItemCommandManager;

    public ExchangePersistenceFacade(
            ExchangeCommandManager exchangeCommandManager,
            ExchangeOutboxCommandManager outboxCommandManager,
            ClaimHistoryCommandManager historyCommandManager,
            OrderItemCommandManager orderItemCommandManager) {
        this.exchangeCommandManager = exchangeCommandManager;
        this.outboxCommandManager = outboxCommandManager;
        this.historyCommandManager = historyCommandManager;
        this.orderItemCommandManager = orderItemCommandManager;
    }

    /** Bundle 내 모든 도메인 객체를 같은 트랜잭션에서 일괄 저장합니다. */
    @Transactional
    public void persistAll(ExchangePersistenceBundle bundle) {
        exchangeCommandManager.persistAll(bundle.claims());
        if (!bundle.outboxes().isEmpty()) {
            outboxCommandManager.persistAll(bundle.outboxes());
        }
        if (!bundle.histories().isEmpty()) {
            historyCommandManager.persistAll(bundle.histories());
        }
        if (!bundle.orderItems().isEmpty()) {
            orderItemCommandManager.persistAll(bundle.orderItems());
        }
    }
}
