package com.ryuqq.marketplace.application.shipment;

import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Shipment Command 테스트 Fixtures.
 *
 * <p>Shipment 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 */
public final class ShipmentCommandFixtures {

    private ShipmentCommandFixtures() {}

    private static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    private static final String DEFAULT_TRACKING_NUMBER = "1234567890";
    private static final String DEFAULT_COURIER_CODE = "CJ";
    private static final String DEFAULT_COURIER_NAME = "CJ대한통운";

    // ===== ConfirmShipmentBatchCommand =====

    public static ConfirmShipmentBatchCommand confirmBatchCommand(String... orderItemIds) {
        return new ConfirmShipmentBatchCommand(List.of(orderItemIds), null);
    }

    public static ConfirmShipmentBatchCommand confirmBatchCommandWithSeller(
            Long sellerId, String... orderItemIds) {
        return new ConfirmShipmentBatchCommand(List.of(orderItemIds), sellerId);
    }

    public static ConfirmShipmentBatchCommand confirmBatchCommand(int count) {
        List<String> ids =
                LongStream.rangeClosed(1, count)
                        .mapToObj(
                                i -> "01940001-0000-7000-8000-000000000" + String.format("%03d", i))
                        .toList();
        return new ConfirmShipmentBatchCommand(ids, null);
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
                                                "01940001-0000-7000-8000-000000000"
                                                        + String.format("%03d", i),
                                                "ORD-20260101-" + String.format("%04d", i) + "-001",
                                                "tracking-" + i,
                                                DEFAULT_COURIER_CODE,
                                                "COURIER"))
                        .toList();
        return new ShipBatchCommand(items);
    }

    public static ShipBatchItem shipBatchItem(
            String orderItemId, String orderItemNumber, String trackingNumber) {
        return new ShipBatchItem(
                orderItemId, orderItemNumber, trackingNumber, DEFAULT_COURIER_CODE, "COURIER");
    }

    // ===== ShipSingleCommand =====

    public static ShipSingleCommand shipSingleCommand() {
        return new ShipSingleCommand(
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_TRACKING_NUMBER,
                DEFAULT_COURIER_CODE,
                DEFAULT_COURIER_NAME,
                "COURIER");
    }

    public static ShipSingleCommand shipSingleCommand(String orderItemId, String trackingNumber) {
        return new ShipSingleCommand(
                orderItemId, trackingNumber, DEFAULT_COURIER_CODE, DEFAULT_COURIER_NAME, "COURIER");
    }

    public static ShipSingleCommand shipSingleCommandWithMethodType(String methodType) {
        return new ShipSingleCommand(
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_TRACKING_NUMBER,
                DEFAULT_COURIER_CODE,
                DEFAULT_COURIER_NAME,
                methodType);
    }
}
