package com.ryuqq.marketplace.application.order.port.out.command;

import com.ryuqq.marketplace.domain.order.aggregate.Order;
import java.util.List;

/** 주문 Command Port. */
public interface OrderCommandPort {

    void persist(Order order);

    void persistAll(List<Order> orders);
}
