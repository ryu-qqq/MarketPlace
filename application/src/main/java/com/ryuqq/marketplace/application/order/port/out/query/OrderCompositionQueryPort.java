package com.ryuqq.marketplace.application.order.port.out.query;

import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import java.util.List;
import java.util.Optional;

/** 주문 Composition 조회 Port. 크로스 테이블 조인을 통한 성능 최적화된 조회. */
public interface OrderCompositionQueryPort {

    List<OrderListResult> searchOrders(OrderSearchCriteria criteria);

    long countOrders(OrderSearchCriteria criteria);

    Optional<OrderDetailResult> findOrderDetail(String orderId);
}
