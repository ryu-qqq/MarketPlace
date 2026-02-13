package com.ryuqq.marketplace.application.shipment.port.in.query;

import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;

/** 배송 상태별 요약 조회 UseCase. */
public interface GetShipmentSummaryUseCase {

    ShipmentSummaryResult execute();
}
