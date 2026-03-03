package com.ryuqq.marketplace.application.order.service.query;

import com.ryuqq.marketplace.application.order.assembler.OrderAssembler;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.application.order.manager.OrderReadManager;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderSummaryUseCase;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 주문 상태별 요약 조회 Service. */
@Service
public class GetOrderSummaryService implements GetOrderSummaryUseCase {

    private final OrderReadManager readManager;
    private final OrderAssembler assembler;

    public GetOrderSummaryService(OrderReadManager readManager, OrderAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public OrderSummaryResult execute() {
        Map<OrderStatus, Long> statusCounts = readManager.countByStatus();
        return assembler.toSummaryResult(statusCounts);
    }
}
