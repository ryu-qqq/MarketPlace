package com.ryuqq.marketplace.domain.shipment.exception;

public class ExternalMappingNotFoundException extends ShipmentException {
    private static final ShipmentErrorCode ERROR_CODE = ShipmentErrorCode.EXTERNAL_MAPPING_NOT_FOUND;

    public ExternalMappingNotFoundException(String orderItemId) {
        super(ERROR_CODE, String.format("orderItemId=%s에 대한 외부 주문 매핑을 찾을 수 없습니다", orderItemId));
    }
}
