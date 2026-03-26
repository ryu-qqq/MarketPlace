package com.ryuqq.marketplace.application.legacy.order.port.in.query;

import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;

/** 레거시 주문 단건 조회 UseCase. */
public interface LegacyOrderQueryUseCase {

    LegacyOrderDetailResult execute(long orderId);
}
