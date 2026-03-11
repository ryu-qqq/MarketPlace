package com.ryuqq.marketplace.domain.shipment.outbox.id;

/** 배송 아웃박스 ID Value Object. */
public record ShipmentOutboxId(Long value) {

    public static ShipmentOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ShipmentOutboxId 값은 null일 수 없습니다");
        }
        return new ShipmentOutboxId(value);
    }

    public static ShipmentOutboxId forNew() {
        return new ShipmentOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
