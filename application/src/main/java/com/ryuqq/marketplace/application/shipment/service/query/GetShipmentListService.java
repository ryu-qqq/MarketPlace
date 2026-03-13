package com.ryuqq.marketplace.application.shipment.service.query;

import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.manager.OrderCompositionReadManager;
import com.ryuqq.marketplace.application.shipment.assembler.ShipmentAssembler;
import com.ryuqq.marketplace.application.shipment.dto.query.ShipmentSearchParams;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentQueryFactory;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.query.GetShipmentListUseCase;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 배송 목록 조회 Service. */
@Service
public class GetShipmentListService implements GetShipmentListUseCase {

    private final ShipmentReadManager readManager;
    private final OrderCompositionReadManager orderReadManager;
    private final ShipmentQueryFactory queryFactory;
    private final ShipmentAssembler assembler;

    public GetShipmentListService(
            ShipmentReadManager readManager,
            OrderCompositionReadManager orderReadManager,
            ShipmentQueryFactory queryFactory,
            ShipmentAssembler assembler) {
        this.readManager = readManager;
        this.orderReadManager = orderReadManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public ShipmentPageResult execute(ShipmentSearchParams params) {
        ShipmentSearchCriteria criteria = queryFactory.createCriteria(params);

        List<Shipment> shipments = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        if (shipments.isEmpty()) {
            return assembler.toPageResult(List.of(), params.page(), params.size(), totalElements);
        }

        List<String> orderItemIds = shipments.stream().map(Shipment::orderItemIdValue).toList();

        Map<String, OrderItemResult> itemMap = orderReadManager.findOrderItemsByIds(orderItemIds);

        List<String> orderIds =
                itemMap.values().stream().map(OrderItemResult::orderId).distinct().toList();

        Map<String, OrderListResult> orderMap = orderReadManager.findOrdersByIds(orderIds);

        List<ShipmentListResult> results = new ArrayList<>();
        for (Shipment shipment : shipments) {
            OrderItemResult item = itemMap.get(shipment.orderItemIdValue());
            if (item == null) {
                continue;
            }
            OrderListResult order = orderMap.get(item.orderId());
            if (order == null) {
                continue;
            }
            results.add(assembler.toListResult(shipment, item, order));
        }

        return assembler.toPageResult(results, params.page(), params.size(), totalElements);
    }
}
