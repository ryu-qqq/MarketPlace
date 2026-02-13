package com.ryuqq.marketplace.application.shipment.service.query;

import com.ryuqq.marketplace.application.shipment.assembler.ShipmentAssembler;
import com.ryuqq.marketplace.application.shipment.dto.query.ShipmentSearchParams;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentQueryFactory;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.query.GetShipmentListUseCase;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 배송 목록 조회 Service. */
@Service
public class GetShipmentListService implements GetShipmentListUseCase {

    private final ShipmentReadManager readManager;
    private final ShipmentQueryFactory queryFactory;
    private final ShipmentAssembler assembler;

    public GetShipmentListService(
            ShipmentReadManager readManager,
            ShipmentQueryFactory queryFactory,
            ShipmentAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public ShipmentPageResult execute(ShipmentSearchParams params) {
        ShipmentSearchCriteria criteria = queryFactory.createCriteria(params);

        List<Shipment> shipments = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        return assembler.toPageResult(shipments, params.page(), params.size(), totalElements);
    }
}
