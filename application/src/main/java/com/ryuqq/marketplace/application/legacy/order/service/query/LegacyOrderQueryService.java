package com.ryuqq.marketplace.application.legacy.order.service.query;

import com.ryuqq.marketplace.application.legacy.order.assembler.LegacyOrderFromMarketAssembler;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderQueryUseCase;
import com.ryuqq.marketplace.application.legacy.order.resolver.LegacyOrderIdResolver;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderDetailUseCase;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 주문 단건 조회 서비스.
 *
 * <p>market 스키마에서 조회 후 레거시 응답 형식으로 변환합니다.
 */
@Service
public class LegacyOrderQueryService implements LegacyOrderQueryUseCase {

    private final LegacyOrderIdResolver idResolver;
    private final GetOrderDetailUseCase getOrderDetailUseCase;
    private final ShipmentReadManager shipmentReadManager;
    private final LegacyOrderFromMarketAssembler assembler;

    public LegacyOrderQueryService(
            LegacyOrderIdResolver idResolver,
            GetOrderDetailUseCase getOrderDetailUseCase,
            ShipmentReadManager shipmentReadManager,
            LegacyOrderFromMarketAssembler assembler) {
        this.idResolver = idResolver;
        this.getOrderDetailUseCase = getOrderDetailUseCase;
        this.shipmentReadManager = shipmentReadManager;
        this.assembler = assembler;
    }

    @Override
    @Transactional(readOnly = true)
    public LegacyOrderDetailResult execute(long orderId) {
        LegacyOrderIdMapping mapping =
                idResolver
                        .resolve(orderId)
                        .orElseThrow(
                                () ->
                                        new com.ryuqq.marketplace.domain.order.exception
                                                .OrderNotFoundException(String.valueOf(orderId)));

        Long orderItemId = mapping.internalOrderItemId();
        var detail = getOrderDetailUseCase.execute(orderItemId);
        Shipment shipment =
                shipmentReadManager.findByOrderItemId(OrderItemId.of(orderItemId)).orElse(null);
        return assembler.toDetailResult(detail, mapping, shipment);
    }
}
