package com.ryuqq.marketplace.application.legacy.order.port.out.query;

import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderHistoryResult;
import java.util.List;
import java.util.Optional;

/** 레거시 주문 조회 Port. */
public interface LegacyOrderQueryPort {

    Optional<LegacyOrderDetailResult> fetchOrderDetail(long orderId);

    List<LegacyOrderDetailResult> fetchOrderList(LegacyOrderSearchParams params);

    long countOrders(LegacyOrderSearchParams params);

    List<LegacyOrderHistoryResult> fetchOrderHistories(List<Long> orderIds);
}
