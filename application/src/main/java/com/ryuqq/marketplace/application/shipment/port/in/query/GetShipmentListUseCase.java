package com.ryuqq.marketplace.application.shipment.port.in.query;

import com.ryuqq.marketplace.application.shipment.dto.query.ShipmentSearchParams;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;

/** 배송 목록 조회 UseCase. */
public interface GetShipmentListUseCase {

    ShipmentPageResult execute(ShipmentSearchParams params);
}
