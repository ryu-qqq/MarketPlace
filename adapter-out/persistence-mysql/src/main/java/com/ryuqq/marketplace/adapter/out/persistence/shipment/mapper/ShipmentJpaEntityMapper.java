package com.ryuqq.marketplace.adapter.out.persistence.shipment.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.ShipmentJpaEntity;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentNumber;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import org.springframework.stereotype.Component;

/** Shipment JPA Entity Mapper. */
@Component
public class ShipmentJpaEntityMapper {

    public ShipmentJpaEntity toEntity(Shipment shipment) {
        ShipmentMethod method = shipment.shipmentMethod();
        return ShipmentJpaEntity.create(
                shipment.idValue(),
                shipment.shipmentNumberValue(),
                shipment.orderId(),
                shipment.orderNumber(),
                shipment.status().name(),
                method != null ? method.type().name() : null,
                method != null ? method.courierCode() : null,
                method != null ? method.courierName() : null,
                shipment.trackingNumber(),
                shipment.orderConfirmedAt(),
                shipment.shippedAt(),
                shipment.deliveredAt(),
                shipment.createdAt(),
                shipment.updatedAt(),
                null);
    }

    public Shipment toDomain(ShipmentJpaEntity entity) {
        ShipmentMethod method = resolveShipmentMethod(entity);
        return Shipment.reconstitute(
                ShipmentId.of(entity.getId()),
                ShipmentNumber.of(entity.getShipmentNumber()),
                entity.getOrderId(),
                entity.getOrderNumber(),
                ShipmentStatus.valueOf(entity.getStatus()),
                method,
                entity.getTrackingNumber(),
                entity.getOrderConfirmedAt(),
                entity.getShippedAt(),
                entity.getDeliveredAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private ShipmentMethod resolveShipmentMethod(ShipmentJpaEntity entity) {
        if (entity.getShipmentMethodType() == null
                && entity.getCourierCode() == null
                && entity.getCourierName() == null) {
            return null;
        }
        if (entity.getShipmentMethodType() == null) {
            return null;
        }
        return ShipmentMethod.of(
                ShipmentMethodType.valueOf(entity.getShipmentMethodType()),
                entity.getCourierCode(),
                entity.getCourierName());
    }
}
