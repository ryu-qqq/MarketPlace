package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.application.cancel.manager.CancelCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.dto.bundle.LegacyOrderConversionBundle;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderIdMappingCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 주문 변환 결과 저장 Facade.
 *
 * <p>Order + Shipment + Cancel/Refund + Mapping을 하나의 트랜잭션으로 저장합니다.
 */
@Component
public class LegacyOrderPersistenceFacade {

    private final OrderCommandManager orderCommandManager;
    private final ShipmentCommandManager shipmentCommandManager;
    private final CancelCommandManager cancelCommandManager;
    private final RefundCommandManager refundCommandManager;
    private final LegacyOrderIdMappingCommandManager mappingCommandManager;

    public LegacyOrderPersistenceFacade(
            OrderCommandManager orderCommandManager,
            ShipmentCommandManager shipmentCommandManager,
            CancelCommandManager cancelCommandManager,
            RefundCommandManager refundCommandManager,
            LegacyOrderIdMappingCommandManager mappingCommandManager) {
        this.orderCommandManager = orderCommandManager;
        this.shipmentCommandManager = shipmentCommandManager;
        this.cancelCommandManager = cancelCommandManager;
        this.refundCommandManager = refundCommandManager;
        this.mappingCommandManager = mappingCommandManager;
    }

    @Transactional
    public void persist(LegacyOrderConversionBundle bundle) {
        orderCommandManager.persist(bundle.order());

        if (bundle.hasShipment()) {
            shipmentCommandManager.persist(bundle.shipment());
        }

        if (bundle.hasCancel()) {
            cancelCommandManager.persist(bundle.cancel());
        }

        if (bundle.hasRefund()) {
            refundCommandManager.persist(bundle.refundClaim());
        }

        mappingCommandManager.persist(bundle.mapping());
    }
}
