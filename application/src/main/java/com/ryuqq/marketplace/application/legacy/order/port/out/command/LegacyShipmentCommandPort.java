package com.ryuqq.marketplace.application.legacy.order.port.out.command;

/** 레거시 배송(shipment) 커맨드 Port. */
public interface LegacyShipmentCommandPort {

    void updateShipmentInfo(long orderId, String invoiceNo, String courierCode, String shipmentType);
}
