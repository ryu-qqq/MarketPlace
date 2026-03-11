package com.ryuqq.marketplace.domain.shipment.aggregate;

import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentErrorCode;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentException;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentNumber;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;

/** 배송 Aggregate Root. */
public class Shipment {

    private final ShipmentId id;
    private final ShipmentNumber shipmentNumber;
    private final OrderItemId orderItemId;
    private ShipmentStatus status;
    private ShipmentMethod shipmentMethod;
    private String trackingNumber;
    private Instant orderConfirmedAt;
    private Instant shippedAt;
    private Instant deliveredAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private Shipment(
            ShipmentId id,
            ShipmentNumber shipmentNumber,
            OrderItemId orderItemId,
            ShipmentStatus status,
            ShipmentMethod shipmentMethod,
            String trackingNumber,
            Instant orderConfirmedAt,
            Instant shippedAt,
            Instant deliveredAt,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.shipmentNumber = shipmentNumber;
        this.orderItemId = orderItemId;
        this.status = status;
        this.shipmentMethod = shipmentMethod;
        this.trackingNumber = trackingNumber;
        this.orderConfirmedAt = orderConfirmedAt;
        this.shippedAt = shippedAt;
        this.deliveredAt = deliveredAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Shipment forNew(
            ShipmentId id, ShipmentNumber shipmentNumber, OrderItemId orderItemId, Instant now) {
        return new Shipment(
                id,
                shipmentNumber,
                orderItemId,
                ShipmentStatus.READY,
                null,
                null,
                null,
                null,
                null,
                now,
                now);
    }

    public static Shipment reconstitute(
            ShipmentId id,
            ShipmentNumber shipmentNumber,
            OrderItemId orderItemId,
            ShipmentStatus status,
            ShipmentMethod shipmentMethod,
            String trackingNumber,
            Instant orderConfirmedAt,
            Instant shippedAt,
            Instant deliveredAt,
            Instant createdAt,
            Instant updatedAt) {
        return new Shipment(
                id,
                shipmentNumber,
                orderItemId,
                status,
                shipmentMethod,
                trackingNumber,
                orderConfirmedAt,
                shippedAt,
                deliveredAt,
                createdAt,
                updatedAt);
    }

    public boolean canPrepare() {
        return this.status == ShipmentStatus.READY;
    }

    public boolean canShip() {
        return this.status == ShipmentStatus.PREPARING;
    }

    /** 발주확인: READY → PREPARING */
    public void prepare(Instant now) {
        validateTransition(ShipmentStatus.READY, ShipmentStatus.PREPARING);
        this.status = ShipmentStatus.PREPARING;
        this.orderConfirmedAt = now;
        this.updatedAt = now;
    }

    /** 송장등록: PREPARING → SHIPPED */
    public void ship(String trackingNumber, ShipmentMethod method, Instant now) {
        validateTransition(ShipmentStatus.PREPARING, ShipmentStatus.SHIPPED);
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new ShipmentException(ShipmentErrorCode.TRACKING_NUMBER_REQUIRED);
        }
        this.status = ShipmentStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
        this.shipmentMethod = method;
        this.shippedAt = now;
        this.updatedAt = now;
    }

    /** 배송중: SHIPPED → IN_TRANSIT */
    public void startTransit(Instant now) {
        validateTransition(ShipmentStatus.SHIPPED, ShipmentStatus.IN_TRANSIT);
        this.status = ShipmentStatus.IN_TRANSIT;
        this.updatedAt = now;
    }

    /** 배송완료: IN_TRANSIT → DELIVERED */
    public void deliver(Instant now) {
        validateTransition(ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED);
        this.status = ShipmentStatus.DELIVERED;
        this.deliveredAt = now;
        this.updatedAt = now;
    }

    /** 배송실패: IN_TRANSIT → FAILED */
    public void fail(Instant now) {
        validateTransition(ShipmentStatus.IN_TRANSIT, ShipmentStatus.FAILED);
        this.status = ShipmentStatus.FAILED;
        this.updatedAt = now;
    }

    /** 취소: PREPARING → CANCELLED */
    public void cancel(Instant now) {
        validateTransition(ShipmentStatus.PREPARING, ShipmentStatus.CANCELLED);
        this.status = ShipmentStatus.CANCELLED;
        this.updatedAt = now;
    }

    private void validateTransition(ShipmentStatus expectedCurrent, ShipmentStatus target) {
        if (this.status != expectedCurrent) {
            throw new ShipmentException(
                    ShipmentErrorCode.INVALID_STATUS_TRANSITION,
                    String.format(
                            "배송 상태를 %s에서 %s(으)로 변경할 수 없습니다. 현재 상태: %s",
                            expectedCurrent, target, this.status));
        }
    }

    // Getters
    public ShipmentId id() {
        return id;
    }

    public String idValue() {
        return id.value();
    }

    public ShipmentNumber shipmentNumber() {
        return shipmentNumber;
    }

    public String shipmentNumberValue() {
        return shipmentNumber.value();
    }

    public OrderItemId orderItemId() {
        return orderItemId;
    }

    public long orderItemIdValue() {
        return orderItemId.value();
    }

    public ShipmentStatus status() {
        return status;
    }

    public ShipmentMethod shipmentMethod() {
        return shipmentMethod;
    }

    public String trackingNumber() {
        return trackingNumber;
    }

    public Instant orderConfirmedAt() {
        return orderConfirmedAt;
    }

    public Instant shippedAt() {
        return shippedAt;
    }

    public Instant deliveredAt() {
        return deliveredAt;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
