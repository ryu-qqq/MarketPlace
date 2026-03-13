package com.ryuqq.marketplace.adapter.out.persistence.shipment;

import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.ShipmentJpaEntity;
import java.time.Instant;

/**
 * ShipmentJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ShipmentJpaEntity 관련 객체들을 생성합니다.
 */
public final class ShipmentJpaEntityFixtures {

    private ShipmentJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_ID = "01944b2a-1234-7fff-8888-abcdef012345";
    public static final String DEFAULT_SHIPMENT_NUMBER = "SHP-20260218-0001";
    public static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    public static final String DEFAULT_STATUS_READY = "READY";
    public static final String DEFAULT_STATUS_SHIPPED = "SHIPPED";
    public static final String DEFAULT_STATUS_DELIVERED = "DELIVERED";
    public static final String DEFAULT_COURIER_CODE = "CJ";
    public static final String DEFAULT_COURIER_NAME = "CJ대한통운";
    public static final String DEFAULT_TRACKING_NUMBER = "1234567890";
    public static final String DEFAULT_SHIPMENT_METHOD_TYPE = "COURIER";

    // ===== Entity Fixtures =====

    /** READY 상태의 신규 배송 Entity 생성. */
    public static ShipmentJpaEntity readyEntity() {
        Instant now = Instant.now();
        return ShipmentJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_STATUS_READY,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null);
    }

    /** READY 상태 배송 Entity 생성 (ID 지정). */
    public static ShipmentJpaEntity readyEntity(String id) {
        Instant now = Instant.now();
        return ShipmentJpaEntity.create(
                id,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_STATUS_READY,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null);
    }

    /** SHIPPED 상태의 배송 Entity 생성. */
    public static ShipmentJpaEntity shippedEntity() {
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);
        return ShipmentJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_STATUS_SHIPPED,
                DEFAULT_SHIPMENT_METHOD_TYPE,
                DEFAULT_COURIER_CODE,
                DEFAULT_COURIER_NAME,
                DEFAULT_TRACKING_NUMBER,
                yesterday,
                now,
                null,
                yesterday,
                now,
                null);
    }

    /** DELIVERED 상태의 배송 Entity 생성. */
    public static ShipmentJpaEntity deliveredEntity() {
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);
        return ShipmentJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_STATUS_DELIVERED,
                DEFAULT_SHIPMENT_METHOD_TYPE,
                DEFAULT_COURIER_CODE,
                DEFAULT_COURIER_NAME,
                DEFAULT_TRACKING_NUMBER,
                yesterday,
                yesterday,
                now,
                yesterday,
                now,
                null);
    }

    /** 삭제된 배송 Entity 생성. */
    public static ShipmentJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return ShipmentJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_STATUS_READY,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                now);
    }

    /** 특정 orderItemId를 가진 READY Entity 생성. */
    public static ShipmentJpaEntity readyEntityWithOrderItemId(String id, String orderItemId) {
        Instant now = Instant.now();
        return ShipmentJpaEntity.create(
                id,
                DEFAULT_SHIPMENT_NUMBER,
                orderItemId,
                DEFAULT_STATUS_READY,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null);
    }

    /** 특정 상태를 가진 Entity 생성. */
    public static ShipmentJpaEntity entityWithStatus(String id, String status) {
        Instant now = Instant.now();
        return ShipmentJpaEntity.create(
                id,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                status,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null);
    }

    /** 배송 방법 정보가 없는 Entity 생성 (method 필드 전부 null). */
    public static ShipmentJpaEntity entityWithoutShipmentMethod() {
        Instant now = Instant.now();
        return ShipmentJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_STATUS_READY,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null);
    }

    /** 송장번호와 배송 방법이 있는 Entity 생성. */
    public static ShipmentJpaEntity entityWithTrackingNumber(String id) {
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);
        return ShipmentJpaEntity.create(
                id,
                DEFAULT_SHIPMENT_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_STATUS_SHIPPED,
                DEFAULT_SHIPMENT_METHOD_TYPE,
                DEFAULT_COURIER_CODE,
                DEFAULT_COURIER_NAME,
                DEFAULT_TRACKING_NUMBER,
                yesterday,
                now,
                null,
                yesterday,
                now,
                null);
    }
}
