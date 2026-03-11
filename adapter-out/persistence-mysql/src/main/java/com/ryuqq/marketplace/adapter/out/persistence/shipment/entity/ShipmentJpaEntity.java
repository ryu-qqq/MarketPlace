package com.ryuqq.marketplace.adapter.out.persistence.shipment.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** Shipment JPA 엔티티. */
@Entity
@Table(name = "shipments")
public class ShipmentJpaEntity extends SoftDeletableEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "shipment_number", nullable = false, length = 50)
    private String shipmentNumber;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "shipment_method_type", length = 30)
    private String shipmentMethodType;

    @Column(name = "courier_code", length = 50)
    private String courierCode;

    @Column(name = "courier_name", length = 100)
    private String courierName;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "order_confirmed_at")
    private Instant orderConfirmedAt;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    protected ShipmentJpaEntity() {
        super();
    }

    private ShipmentJpaEntity(
            String id,
            String shipmentNumber,
            Long orderItemId,
            String status,
            String shipmentMethodType,
            String courierCode,
            String courierName,
            String trackingNumber,
            Instant orderConfirmedAt,
            Instant shippedAt,
            Instant deliveredAt,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.shipmentNumber = shipmentNumber;
        this.orderItemId = orderItemId;
        this.status = status;
        this.shipmentMethodType = shipmentMethodType;
        this.courierCode = courierCode;
        this.courierName = courierName;
        this.trackingNumber = trackingNumber;
        this.orderConfirmedAt = orderConfirmedAt;
        this.shippedAt = shippedAt;
        this.deliveredAt = deliveredAt;
    }

    public static ShipmentJpaEntity create(
            String id,
            String shipmentNumber,
            Long orderItemId,
            String status,
            String shipmentMethodType,
            String courierCode,
            String courierName,
            String trackingNumber,
            Instant orderConfirmedAt,
            Instant shippedAt,
            Instant deliveredAt,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ShipmentJpaEntity(
                id,
                shipmentNumber,
                orderItemId,
                status,
                shipmentMethodType,
                courierCode,
                courierName,
                trackingNumber,
                orderConfirmedAt,
                shippedAt,
                deliveredAt,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public String getId() {
        return id;
    }

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public String getStatus() {
        return status;
    }

    public String getShipmentMethodType() {
        return shipmentMethodType;
    }

    public String getCourierCode() {
        return courierCode;
    }

    public String getCourierName() {
        return courierName;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Instant getOrderConfirmedAt() {
        return orderConfirmedAt;
    }

    public Instant getShippedAt() {
        return shippedAt;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }
}
