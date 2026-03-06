package com.ryuqq.marketplace.application.order.port.in.query;

import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;

/** 주문 상세 조회 UseCase. */
public interface GetOrderDetailUseCase {

    OrderDetailResult execute(String orderId);
}
