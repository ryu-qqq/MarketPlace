package com.ryuqq.marketplace.application.order.manager;

import com.ryuqq.marketplace.application.order.port.out.query.OrderQueryPort;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.exception.OrderNotFoundException;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Order Read Manager. */
@Component
public class OrderReadManager {

    private final OrderQueryPort queryPort;

    public OrderReadManager(OrderQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Order getById(OrderId id) {
        return queryPort.findById(id).orElseThrow(() -> new OrderNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public Order getByOrderNumber(String orderNumber) {
        return queryPort
                .findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
    }

    @Transactional(readOnly = true)
    public boolean existsByExternalOrderNo(long salesChannelId, String externalOrderNo) {
        return queryPort.existsByExternalOrderNo(salesChannelId, externalOrderNo);
    }

    @Transactional(readOnly = true)
    public List<Order> findByCriteria(OrderSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(OrderSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public Map<OrderStatus, Long> countByStatus() {
        return queryPort.countByStatus();
    }
}
