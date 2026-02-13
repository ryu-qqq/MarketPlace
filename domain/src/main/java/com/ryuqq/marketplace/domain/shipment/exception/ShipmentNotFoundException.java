package com.ryuqq.marketplace.domain.shipment.exception;

/** 배송 정보를 찾을 수 없는 경우 예외. */
public class ShipmentNotFoundException extends ShipmentException {

    private static final ShipmentErrorCode ERROR_CODE = ShipmentErrorCode.SHIPMENT_NOT_FOUND;

    public ShipmentNotFoundException() {
        super(ERROR_CODE);
    }

    public ShipmentNotFoundException(String shipmentId) {
        super(ERROR_CODE, String.format("ID가 %s인 배송 정보를 찾을 수 없습니다", shipmentId));
    }
}
