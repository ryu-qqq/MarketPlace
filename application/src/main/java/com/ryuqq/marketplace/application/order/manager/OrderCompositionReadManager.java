package com.ryuqq.marketplace.application.order.manager;

import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.port.out.query.OrderCompositionQueryPort;
import com.ryuqq.marketplace.domain.order.exception.OrderNotFoundException;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 주문 Composition 조회 매니저. 크로스 테이블 조인을 통한 성능 최적화된 조회. */
@Component
public class OrderCompositionReadManager {

    private final OrderCompositionQueryPort compositionQueryPort;

    public OrderCompositionReadManager(OrderCompositionQueryPort compositionQueryPort) {
        this.compositionQueryPort = compositionQueryPort;
    }

    @Transactional(readOnly = true)
    public List<OrderListResult> searchOrders(OrderSearchCriteria criteria) {
        return compositionQueryPort.searchOrders(criteria);
    }

    @Transactional(readOnly = true)
    public long countOrders(OrderSearchCriteria criteria) {
        return compositionQueryPort.countOrders(criteria);
    }

    @Transactional(readOnly = true)
    public OrderDetailResult getOrderDetail(String orderId) {
        return compositionQueryPort
                .findOrderDetail(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
