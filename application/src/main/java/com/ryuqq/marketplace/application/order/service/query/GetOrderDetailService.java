package com.ryuqq.marketplace.application.order.service.query;

import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;
import com.ryuqq.marketplace.application.order.manager.OrderCompositionReadManager;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderDetailUseCase;
import org.springframework.stereotype.Service;

/** 주문 상세 조회 Service. */
@Service
public class GetOrderDetailService implements GetOrderDetailUseCase {

    private final OrderCompositionReadManager compositionReadManager;

    public GetOrderDetailService(OrderCompositionReadManager compositionReadManager) {
        this.compositionReadManager = compositionReadManager;
    }

    @Override
    public OrderDetailResult execute(String orderId) {
        return compositionReadManager.getOrderDetail(orderId);
    }
}
