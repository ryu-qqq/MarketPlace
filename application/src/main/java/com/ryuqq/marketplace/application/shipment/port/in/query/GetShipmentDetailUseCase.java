package com.ryuqq.marketplace.application.shipment.port.in.query;

import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;

/** 배송 상세 조회 UseCase. */
public interface GetShipmentDetailUseCase {

    ShipmentDetailResult execute(String shipmentId);
}
