package com.ryuqq.marketplace.application.shipment;

import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Shipment Command 테스트 Fixtures.
 *
 * <p>Shipment 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 */
public final class ShipmentCommandFixtures {

    private ShipmentCommandFixtures() {}

    private static final String DEFAULT_SHIPMENT_ID = "01944b2a-1234-7fff-8888-abcdef012345";
    private static final String DEFAULT_ORDER_ID = "ORD-20260218-9999";
    private static final String DEFAULT_TRACKING_NUMBER = "1234567890";
    private static final String DEFAULT_COURIER_CODE = "CJ";
    private static final String DEFAULT_COURIER_NAME = "CJ대한통운";

    // ===== ConfirmShipmentBatchCommand =====

    public static ConfirmShipmentBatchCommand confirmBatchCommand(String... shipmentIds) {
        return new ConfirmShipmentBatchCommand(List.of(shipmentIds));
    }

    public static ConfirmShipmentBatchCommand confirmBatchCommand(int count) {
        List<String> ids =
                IntStream.rangeClosed(1, count).mapToObj(i -> "shipment-id-" + i).toList();
        return new ConfirmShipmentBatchCommand(ids);
    }

    // ===== ShipBatchCommand =====

    public static ShipBatchCommand shipBatchCommand(ShipBatchItem... items) {
        return new ShipBatchCommand(List.of(items));
    }

    public static ShipBatchCommand shipBatchCommand(int count) {
        List<ShipBatchItem> items =
                IntStream.rangeClosed(1, count)
                        .mapToObj(
                                i ->
                                        new ShipBatchItem(
                                                "shipment-id-" + i,
                                                "tracking-" + i,
                                                DEFAULT_COURIER_CODE,
                                                DEFAULT_COURIER_NAME,
                                                "COURIER"))
                        .toList();
        return new ShipBatchCommand(items);
    }

    public static ShipBatchItem shipBatchItem(String shipmentId, String trackingNumber) {
        return new ShipBatchItem(
                shipmentId, trackingNumber, DEFAULT_COURIER_CODE, DEFAULT_COURIER_NAME, "COURIER");
    }

    // ===== ShipSingleCommand =====

    public static ShipSingleCommand shipSingleCommand() {
        return new ShipSingleCommand(
                DEFAULT_ORDER_ID,
                DEFAULT_TRACKING_NUMBER,
                DEFAULT_COURIER_CODE,
                DEFAULT_COURIER_NAME,
                "COURIER");
    }

    public static ShipSingleCommand shipSingleCommand(String orderId, String trackingNumber) {
        return new ShipSingleCommand(
                orderId, trackingNumber, DEFAULT_COURIER_CODE, DEFAULT_COURIER_NAME, "COURIER");
    }

    public static ShipSingleCommand shipSingleCommandWithMethodType(String methodType) {
        return new ShipSingleCommand(
                DEFAULT_ORDER_ID,
                DEFAULT_TRACKING_NUMBER,
                DEFAULT_COURIER_CODE,
                DEFAULT_COURIER_NAME,
                methodType);
    }
}
