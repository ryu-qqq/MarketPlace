package com.ryuqq.marketplace.application.order.port.in.query;

import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.OrderPageResult;

/** 주문 목록 조회 UseCase. */
public interface GetOrderListUseCase {

    OrderPageResult execute(OrderSearchParams params);
}
