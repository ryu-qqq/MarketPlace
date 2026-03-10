package com.ryuqq.marketplace.application.order.port.in.query;

import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;

/** 상품주문(아이템 단위) 목록 조회 UseCase. */
public interface GetProductOrderListUseCase {

    ProductOrderPageResult execute(OrderSearchParams params);
}
