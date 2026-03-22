package com.ryuqq.marketplace.application.legacy.order.port.in.query;

import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderPageResult;

/** 레거시 주문 목록 조회 UseCase. */
public interface LegacyOrderListQueryUseCase {

    LegacyOrderPageResult execute(LegacyOrderSearchParams params);
}
