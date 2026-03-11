package com.ryuqq.marketplace.application.shipment.port.out.query;

import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Shipment Query Port. */
public interface ShipmentQueryPort {

    Optional<Shipment> findById(ShipmentId id);

    Optional<Shipment> findByOrderItemId(OrderItemId orderItemId);

    List<Shipment> findByOrderItemIds(List<OrderItemId> orderItemIds);

    List<Shipment> findByCriteria(ShipmentSearchCriteria criteria);

    long countByCriteria(ShipmentSearchCriteria criteria);

    Map<ShipmentStatus, Long> countByStatus();
}
