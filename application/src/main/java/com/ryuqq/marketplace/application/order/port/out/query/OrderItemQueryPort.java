package com.ryuqq.marketplace.application.order.port.out.query;

import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.util.List;
import java.util.Map;

/** 주문상품 Query Port. */
public interface OrderItemQueryPort {

    List<OrderItem> findAllByIds(List<String> orderItemIds);

    Map<OrderItemStatus, Long> countByStatus();
}

