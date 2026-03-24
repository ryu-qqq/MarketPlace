package com.ryuqq.marketplace.application.order.port.out.query;

import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** 주문상품 Query Port. */
public interface OrderItemQueryPort {

    List<OrderItem> findAllByIds(List<OrderItemId> orderItemIds);

    Optional<OrderItem> findByOrderItemNumber(String orderItemNumber);

    Map<OrderItemStatus, Long> countByStatus();
}
