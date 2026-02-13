package com.ryuqq.marketplace.application.shipment.manager;

import com.ryuqq.marketplace.application.shipment.port.out.query.ShipmentQueryPort;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentNotFoundException;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Shipment Read Manager. */
@Component
public class ShipmentReadManager {

    private final ShipmentQueryPort queryPort;

    public ShipmentReadManager(ShipmentQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Shipment getById(ShipmentId id) {
        return queryPort.findById(id).orElseThrow(() -> new ShipmentNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public Shipment getByOrderId(String orderId) {
        return queryPort
                .findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException(orderId));
    }

    @Transactional(readOnly = true)
    public List<Shipment> findByCriteria(ShipmentSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ShipmentSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public Map<ShipmentStatus, Long> countByStatus() {
        return queryPort.countByStatus();
    }
}
