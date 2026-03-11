package com.ryuqq.marketplace.application.shipment.service.query;

import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailData;
import com.ryuqq.marketplace.application.order.manager.OrderCompositionReadManager;
import com.ryuqq.marketplace.application.shipment.assembler.ShipmentAssembler;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.query.GetShipmentDetailUseCase;
import com.ryuqq.marketplace.domain.order.exception.OrderNotFoundException;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import org.springframework.stereotype.Service;

/** 배송 상세 조회 Service. */
@Service
public class GetShipmentDetailService implements GetShipmentDetailUseCase {

    private final ShipmentReadManager readManager;
    private final OrderCompositionReadManager orderReadManager;
    private final ShipmentAssembler assembler;

    public GetShipmentDetailService(
            ShipmentReadManager readManager,
            OrderCompositionReadManager orderReadManager,
            ShipmentAssembler assembler) {
        this.readManager = readManager;
        this.orderReadManager = orderReadManager;
        this.assembler = assembler;
    }

    @Override
    public ShipmentDetailResult execute(String shipmentId) {
        Shipment shipment = readManager.getById(ShipmentId.of(shipmentId));

        ProductOrderDetailData detailData =
                orderReadManager
                        .findProductOrderDetail(shipment.orderItemIdValue())
                        .orElseThrow(
                                () ->
                                        new OrderNotFoundException(
                                                String.valueOf(shipment.orderItemIdValue())));

        return assembler.toDetailResult(shipment, detailData);
    }
}
