package com.ryuqq.marketplace.application.order.manager;

import com.ryuqq.marketplace.application.order.port.out.query.OrderItemQueryPort;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public List<OrderItem> findAllByIds(List<OrderItemId> orderItemIds) {
        return queryPort.findAllByIds(orderItemIds);
    }

    @Transactional(readOnly = true)
    public Optional<OrderItem> findById(OrderItemId orderItemId) {
        return queryPort.findAllByIds(List.of(orderItemId)).stream().findFirst();
    }

    @Transactional(readOnly = true)
    public Map<OrderItemStatus, Long> countByStatus() {
        return queryPort.countByStatus();
    }
}
