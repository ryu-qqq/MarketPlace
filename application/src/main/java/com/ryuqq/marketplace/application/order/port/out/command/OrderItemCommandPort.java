package com.ryuqq.marketplace.application.order.port.out.command;

import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.List;

/** 주문상품 Command Port. */
public interface OrderItemCommandPort {

    void persistAll(List<OrderItem> orderItems);

    void updateAll(List<OrderItem> orderItems);
}
