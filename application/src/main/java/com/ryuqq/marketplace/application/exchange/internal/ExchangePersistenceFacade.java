package com.ryuqq.marketplace.application.exchange.internal;

import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 교환 퍼시스트 파사드.
 *
 * <p>ExchangeClaim + ExchangeOutbox + OrderItem 상태 변경을 같은 트랜잭션에서 일괄 처리합니다.
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

    @Transactional
    public void persistWithOutbox(ExchangeClaim claim, ExchangeOutbox outbox) {
        exchangeCommandManager.persist(claim);
        outboxCommandManager.persist(outbox);
    }

    @Transactional
    public void persistAllWithOutboxes(List<ExchangeClaim> claims, List<ExchangeOutbox> outboxes) {
        exchangeCommandManager.persistAll(claims);
        outboxCommandManager.persistAll(outboxes);
    }

    /** ExchangeClaim + Outbox + History 일괄 저장 (신규 생성 시). */
    @Transactional
    public void persistAllWithOutboxesAndHistories(
            List<ExchangeClaim> claims,
            List<ExchangeOutbox> outboxes,
            List<ClaimHistory> histories) {
        exchangeCommandManager.persistAll(claims);
        outboxCommandManager.persistAll(outboxes);
        historyCommandManager.persistAll(histories);
    }

    @Transactional
    public void persistClaimsWithOutboxes(
            List<ExchangeClaim> claims, List<ExchangeOutbox> outboxes) {
        exchangeCommandManager.persistAll(claims);
        outboxCommandManager.persistAll(outboxes);
    }

    /** ExchangeClaim + Outbox + History 일괄 저장 (수거완료/재배송/거절 시). */
    @Transactional
    public void persistClaimsWithOutboxesAndHistories(
            List<ExchangeClaim> claims,
            List<ExchangeOutbox> outboxes,
            List<ClaimHistory> histories) {
        exchangeCommandManager.persistAll(claims);
        outboxCommandManager.persistAll(outboxes);
        historyCommandManager.persistAll(histories);
    }

    /** ExchangeClaim + History 일괄 저장 (Outbox 불필요한 경우 — 네이버 API 호출 없음). */
    @Transactional
    public void persistClaimsWithHistories(
            List<ExchangeClaim> claims, List<ClaimHistory> histories) {
        exchangeCommandManager.persistAll(claims);
        historyCommandManager.persistAll(histories);
    }

    /** 신규 생성 시 Claim + History 저장 (Outbox 불필요). */
    @Transactional
    public void persistAllWithHistories(
            List<ExchangeClaim> claims, List<ClaimHistory> histories) {
        exchangeCommandManager.persistAll(claims);
        historyCommandManager.persistAll(histories);
    }

    /** Claim + History + OrderItem 상태 변경 저장 (요청/완료 시). */
    @Transactional
    public void persistAllWithHistoriesAndOrderItems(
            List<ExchangeClaim> claims,
            List<ClaimHistory> histories,
            List<OrderItem> orderItems) {
        exchangeCommandManager.persistAll(claims);
        historyCommandManager.persistAll(histories);
        orderItemCommandManager.persistAll(orderItems);
    }

    /** Claim + History + OrderItem 상태 변경 저장 (완료 시). */
    @Transactional
    public void persistClaimsWithHistoriesAndOrderItems(
            List<ExchangeClaim> claims,
            List<ClaimHistory> histories,
            List<OrderItem> orderItems) {
        exchangeCommandManager.persistAll(claims);
        historyCommandManager.persistAll(histories);
        orderItemCommandManager.persistAll(orderItems);
    }
}
