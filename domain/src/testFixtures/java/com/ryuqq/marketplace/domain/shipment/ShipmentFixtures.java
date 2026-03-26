package com.ryuqq.marketplace.domain.shipment;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentNumber;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;

/**
 * Shipment 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 Shipment 관련 객체들을 생성합니다.
 */
public final class ShipmentFixtures {

    private ShipmentFixtures() {}

    // ===== 기본 상수 =====
    private static final String DEFAULT_SHIPMENT_ID = "01944b2a-1234-7fff-8888-abcdef012345";
    private static final String DEFAULT_SHIPMENT_NUMBER = "SHP-20260218-0001";
    private static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    private static final String DEFAULT_TRACKING_NUMBER = "1234567890";

    // ===== ID Fixtures =====

    public static ShipmentId defaultShipmentId() {
        return ShipmentId.of(DEFAULT_SHIPMENT_ID);
    }

    public static ShipmentId shipmentId(String value) {
        return ShipmentId.of(value);
    }

    // ===== OrderItemId Fixtures =====

    public static OrderItemId defaultOrderItemId() {
        return OrderItemId.of(DEFAULT_ORDER_ITEM_ID);
    }

    public static OrderItemId orderItemId(String value) {
        return OrderItemId.of(value);
    }

    // ===== ShipmentNumber Fixtures =====

    public static ShipmentNumber defaultShipmentNumber() {
        return ShipmentNumber.of(DEFAULT_SHIPMENT_NUMBER);
    }

    public static ShipmentNumber shipmentNumber(String value) {
        return ShipmentNumber.of(value);
    }

    // ===== ShipmentMethod Fixtures =====

    public static ShipmentMethod defaultShipmentMethod() {
        return ShipmentMethod.of(ShipmentMethodType.COURIER, "CJ", "CJ대한통운");
    }

    public static ShipmentMethod quickShipmentMethod() {
        return ShipmentMethod.of(ShipmentMethodType.QUICK, null, "퀵배송");
    }

    public static ShipmentMethod visitShipmentMethod() {
        return ShipmentMethod.of(ShipmentMethodType.VISIT, null, "방문수령");
    }

    // ===== 신규 배송 생성 (forNew - READY) =====

    public static Shipment newShipment() {
        return Shipment.forNew(
                defaultShipmentId(),
                defaultShipmentNumber(),
                defaultOrderItemId(),
                CommonVoFixtures.now());
    }

    // ===== reconstitute - 상태별 =====

    public static Shipment readyShipment() {
        return Shipment.reconstitute(
                defaultShipmentId(),
                defaultShipmentNumber(),
                defaultOrderItemId(),
                ShipmentStatus.READY,
                null,
                null,
                null,
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Shipment preparingShipment() {
        return Shipment.reconstitute(
                defaultShipmentId(),
                defaultShipmentNumber(),
                defaultOrderItemId(),
                ShipmentStatus.PREPARING,
                null,
                null,
                CommonVoFixtures.yesterday(),
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Shipment shippedShipment() {
        return Shipment.reconstitute(
                defaultShipmentId(),
                defaultShipmentNumber(),
                defaultOrderItemId(),
                ShipmentStatus.SHIPPED,
                defaultShipmentMethod(),
                DEFAULT_TRACKING_NUMBER,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Shipment inTransitShipment() {
        return Shipment.reconstitute(
                defaultShipmentId(),
                defaultShipmentNumber(),
                defaultOrderItemId(),
                ShipmentStatus.IN_TRANSIT,
                defaultShipmentMethod(),
                DEFAULT_TRACKING_NUMBER,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static Shipment deliveredShipment() {
        return Shipment.reconstitute(
                defaultShipmentId(),
                defaultShipmentNumber(),
                defaultOrderItemId(),
                ShipmentStatus.DELIVERED,
                defaultShipmentMethod(),
                DEFAULT_TRACKING_NUMBER,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    public static Shipment failedShipment() {
        return Shipment.reconstitute(
                defaultShipmentId(),
                defaultShipmentNumber(),
                defaultOrderItemId(),
                ShipmentStatus.FAILED,
                defaultShipmentMethod(),
                DEFAULT_TRACKING_NUMBER,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    public static Shipment cancelledShipment() {
        return Shipment.reconstitute(
                defaultShipmentId(),
                defaultShipmentNumber(),
                defaultOrderItemId(),
                ShipmentStatus.CANCELLED,
                null,
                null,
                CommonVoFixtures.yesterday(),
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }
}
