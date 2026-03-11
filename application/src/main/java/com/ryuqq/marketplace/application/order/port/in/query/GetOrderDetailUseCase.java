package com.ryuqq.marketplace.application.order.port.in.query;

import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;

/** 상품주문 상세 조회 UseCase (V5). */
public interface GetOrderDetailUseCase {

    ProductOrderDetailResult execute(long orderItemId);
}
