package com.ryuqq.marketplace.application.order.port.out.query;

import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 주문 Query Port. */
public interface OrderQueryPort {

    Optional<Order> findById(OrderId id);

    Optional<Order> findByOrderNumber(String orderNumber);

    boolean existsByExternalOrderNo(long salesChannelId, String externalOrderNo);

    List<Order> findByCriteria(OrderSearchCriteria criteria);

    long countByCriteria(OrderSearchCriteria criteria);

    Map<OrderStatus, Long> countByStatus();
}
