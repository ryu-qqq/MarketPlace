package com.ryuqq.marketplace.application.cancel.internal;

import com.ryuqq.marketplace.application.cancel.manager.CancelCommandManager;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxCommandManager;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 취소 퍼시스트 파사드.
 *
 * <p>Cancel + CancelOutbox + ClaimHistory + OrderItem + Shipment 상태 변경을 같은 트랜잭션에서 일괄 처리합니다.
 */
@Component
public class CancelPersistenceFacade {

    private final CancelCommandManager cancelCommandManager;
    private final CancelOutboxCommandManager outboxCommandManager;
    private final ClaimHistoryCommandManager historyCommandManager;
    private final OrderItemCommandManager orderItemCommandManager;
    private final ShipmentCommandManager shipmentCommandManager;

    public CancelPersistenceFacade(
            CancelCommandManager cancelCommandManager,
            CancelOutboxCommandManager outboxCommandManager,
            ClaimHistoryCommandManager historyCommandManager,
            OrderItemCommandManager orderItemCommandManager,
            ShipmentCommandManager shipmentCommandManager) {
        this.cancelCommandManager = cancelCommandManager;
        this.outboxCommandManager = outboxCommandManager;
        this.historyCommandManager = historyCommandManager;
        this.orderItemCommandManager = orderItemCommandManager;
        this.shipmentCommandManager = shipmentCommandManager;
    }

    /** Bundle 내 모든 도메인 객체를 같은 트랜잭션에서 일괄 저장합니다. */
    @Transactional
    public void persistAll(CancelPersistenceBundle bundle) {
        cancelCommandManager.persistAll(bundle.cancels());
        outboxCommandManager.persistAll(bundle.outboxes());
        if (!bundle.histories().isEmpty()) {
            historyCommandManager.persistAll(bundle.histories());
        }
        if (!bundle.orderItems().isEmpty()) {
            orderItemCommandManager.persistAll(bundle.orderItems());
        }
        if (!bundle.shipments().isEmpty()) {
            shipmentCommandManager.persistAll(bundle.shipments());
        }
    }
}
