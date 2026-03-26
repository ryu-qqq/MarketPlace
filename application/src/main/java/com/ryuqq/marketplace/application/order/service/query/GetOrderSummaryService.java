package com.ryuqq.marketplace.application.order.service.query;

import com.ryuqq.marketplace.application.order.assembler.OrderAssembler;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderSummaryUseCase;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 주문상품 상태별 요약 조회 Service. */
@Service
public class GetOrderSummaryService implements GetOrderSummaryUseCase {

    private final OrderItemReadManager readManager;
    private final OrderAssembler assembler;

    public GetOrderSummaryService(OrderItemReadManager readManager, OrderAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public OrderSummaryResult execute() {
        Map<OrderItemStatus, Long> statusCounts = readManager.countByStatus();
        return assembler.toSummaryResult(statusCounts);
    }
}
