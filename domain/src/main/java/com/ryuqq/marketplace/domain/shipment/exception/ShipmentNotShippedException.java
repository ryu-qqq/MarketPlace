package com.ryuqq.marketplace.domain.shipment.exception;

/**
 * 운송장 미등록 상태에서 취소 거부를 시도할 때 발생하는 예외.
 *
 * <p>네이버 정책: 운송장이 등록된(SHIPPED 이상) 상태여야 취소 거부가 가능합니다.
 */
public class ShipmentNotShippedException extends ShipmentException {

    public ShipmentNotShippedException(String cancelId) {
        super(
                ShipmentErrorCode.SHIPMENT_NOT_SHIPPED,
                "운송장이 등록되지 않은 주문은 취소 거부할 수 없습니다. 먼저 운송장을 등록해주세요. cancelId=" + cancelId);
    }
}
