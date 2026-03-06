package com.ryuqq.marketplace.application.order.service.query;

import com.ryuqq.marketplace.application.order.assembler.OrderAssembler;
import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;
import com.ryuqq.marketplace.application.order.manager.OrderReadManager;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderDetailUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import org.springframework.stereotype.Service;

/** 주문 상세 조회 Service. */
@Service
public class GetOrderDetailService implements GetOrderDetailUseCase {

    private final OrderReadManager readManager;
    private final OrderAssembler assembler;

    public GetOrderDetailService(OrderReadManager readManager, OrderAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public OrderDetailResult execute(String orderId) {
        Order order = readManager.getById(OrderId.of(orderId));
        return assembler.toDetailResult(order);
    }
}
