package com.ryuqq.marketplace.application.order.manager;

import com.ryuqq.marketplace.application.order.port.out.command.OrderItemCommandPort;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 주문상품 Write Manager. */
@Component
public class OrderItemCommandManager {

    private final OrderItemCommandPort commandPort;

    public OrderItemCommandManager(OrderItemCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persistAll(List<OrderItem> orderItems) {
        commandPort.persistAll(orderItems);
    }
}
