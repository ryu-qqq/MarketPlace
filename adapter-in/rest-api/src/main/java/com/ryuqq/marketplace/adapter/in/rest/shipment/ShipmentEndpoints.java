package com.ryuqq.marketplace.adapter.in.rest.shipment;

/** Shipment API 엔드포인트 상수. */
public final class ShipmentEndpoints {

    private ShipmentEndpoints() {}

    private static final String BASE = "/api/v1/market";
    public static final String SHIPMENTS = BASE + "/shipments";
    public static final String SHIPMENT_ID = "/{shipmentId}";
    public static final String SUMMARY = "/summary";
    public static final String CONFIRM_BATCH = "/confirm/batch";
    public static final String SHIP_BATCH = "/ship/batch";
    public static final String ORDERS = BASE + "/orders";
    public static final String SHIP_SINGLE = "/{orderId}/ship";
}
