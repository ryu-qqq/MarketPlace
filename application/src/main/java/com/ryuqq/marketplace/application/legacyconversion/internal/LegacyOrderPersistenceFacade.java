package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.application.cancel.manager.CancelCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.dto.bundle.LegacyOrderConversionBundle;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderIdMappingCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 주문 변환 결과 저장 Facade.
 *
 * <p>Order persist 후 auto_increment로 할당된 orderItemId를 Shipment/Cancel/Refund/Mapping에 세팅하고 저장합니다.
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
        // 1. Order + OrderItem persist → auto_increment ID 할당
        orderCommandManager.persist(bundle.order());

        // 2. persist 후 orderItemId 추출
        OrderItemId orderItemId = extractOrderItemId(bundle.order());

        // 3. orderItemId를 Shipment/Cancel/Refund에 세팅 후 저장
        if (bundle.hasShipment()) {
            bundle.shipment().assignOrderItemId(orderItemId);
            shipmentCommandManager.persist(bundle.shipment());
        }

        if (bundle.hasCancel()) {
            bundle.cancel().assignOrderItemId(orderItemId);
            cancelCommandManager.persist(bundle.cancel());
        }

        if (bundle.hasRefund()) {
            bundle.refundClaim().assignOrderItemId(orderItemId);
            refundCommandManager.persist(bundle.refundClaim());
        }

        // 4. Mapping에도 orderItemId 세팅
        bundle.mapping().assignOrderItemId(orderItemId.value());
        mappingCommandManager.persist(bundle.mapping());
    }

    private OrderItemId extractOrderItemId(Order order) {
        return order.items().stream()
                .findFirst()
                .map(OrderItem::id)
                .orElseThrow(() -> new IllegalStateException("Order에 OrderItem이 없습니다"));
    }
}
