package com.ryuqq.marketplace.application.order.manager;

import com.ryuqq.marketplace.application.order.port.out.query.OrderItemQueryPort;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 주문상품 Read Manager. */
@Component
public class OrderItemReadManager {

    private final OrderItemQueryPort queryPort;

    public OrderItemReadManager(OrderItemQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<OrderItem> findAllByIds(List<Long> orderItemIds) {
        return queryPort.findAllByIds(orderItemIds);
    }
}
