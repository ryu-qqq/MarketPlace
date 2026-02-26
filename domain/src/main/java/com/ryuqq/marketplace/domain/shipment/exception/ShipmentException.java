package com.ryuqq.marketplace.domain.shipment.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 배송 도메인 예외. */
public class ShipmentException extends DomainException {

    public ShipmentException(ShipmentErrorCode errorCode) {
        super(errorCode);
    }

    public ShipmentException(ShipmentErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ShipmentException(ShipmentErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
