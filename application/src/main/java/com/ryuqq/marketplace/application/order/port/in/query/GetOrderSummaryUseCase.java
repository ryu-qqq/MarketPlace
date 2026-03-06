package com.ryuqq.marketplace.application.order.port.in.query;

import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;

/** 주문 상태별 요약 조회 UseCase. */
public interface GetOrderSummaryUseCase {

    OrderSummaryResult execute();
}
