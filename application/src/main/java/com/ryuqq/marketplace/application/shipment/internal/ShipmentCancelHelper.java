package com.ryuqq.marketplace.application.shipment.internal;

import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PREPARING 상태 Shipment 취소 헬퍼.
 *
 * <p>취소/승인 시 연관 Shipment를 조회하여 PREPARING 상태인 것만 취소합니다. Cancel, Order 양쪽에서 공통으로 사용합니다.
 */
@Component
public class ShipmentCancelHelper {

    private final ShipmentReadManager shipmentReadManager;

    public ShipmentCancelHelper(ShipmentReadManager shipmentReadManager) {
        this.shipmentReadManager = shipmentReadManager;
    }

    /**
     * orderItemIds에 연결된 PREPARING 상태 Shipment를 조회하여 취소합니다.
     *
     * @param orderItemIds 취소 대상 주문상품 ID 목록
     * @param now 취소 시간
     * @return 취소 처리된 Shipment 목록 (persist는 호출자 책임)
     */
    public List<Shipment> cancelPreparingShipments(List<OrderItemId> orderItemIds, Instant now) {
        if (orderItemIds.isEmpty()) {
            return List.of();
        }
        List<Shipment> shipments = shipmentReadManager.findByOrderItemIds(orderItemIds);
        List<Shipment> cancelled = new ArrayList<>();
        for (Shipment shipment : shipments) {
            if (shipment.status() == ShipmentStatus.PREPARING) {
                shipment.cancel(now);
                cancelled.add(shipment);
            }
        }
        return cancelled;
    }
}
