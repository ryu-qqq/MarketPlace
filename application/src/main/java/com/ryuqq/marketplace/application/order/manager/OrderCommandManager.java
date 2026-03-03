package com.ryuqq.marketplace.application.order.manager;

import com.ryuqq.marketplace.application.order.port.out.command.OrderCommandPort;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Order Command Manager. */
@Component
public class OrderCommandManager {

    private final OrderCommandPort commandPort;

    public OrderCommandManager(OrderCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(Order order) {
        commandPort.persist(order);
    }

    @Transactional
    public void persistAll(List<Order> orders) {
        commandPort.persistAll(orders);
    }
}
