package com.ryuqq.marketplace.application.order.port.out.query;

import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.List;

/** 주문상품 Query Port. */
public interface OrderItemQueryPort {

    List<OrderItem> findAllByIds(List<Long> orderItemIds);
}
