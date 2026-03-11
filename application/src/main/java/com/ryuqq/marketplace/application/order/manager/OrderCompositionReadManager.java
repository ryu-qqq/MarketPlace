package com.ryuqq.marketplace.application.order.manager;

import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailData;
import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.port.out.query.OrderCompositionQueryPort;
import com.ryuqq.marketplace.domain.order.exception.OrderNotFoundException;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    // ==================== V5 상품주문 리스트 ====================

    @Transactional(readOnly = true)
    public List<OrderItemResult> searchProductOrders(OrderSearchCriteria criteria) {
        return compositionQueryPort.searchProductOrders(criteria);
    }

    @Transactional(readOnly = true)
    public long countProductOrders(OrderSearchCriteria criteria) {
        return compositionQueryPort.countProductOrders(criteria);
    }

    @Transactional(readOnly = true)
    public Map<String, OrderListResult> findOrdersByIds(List<String> orderIds) {
        return compositionQueryPort.findOrdersByIds(orderIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<OrderCancelResult>> findCancelsByItemIds(List<Long> orderItemIds) {
        return compositionQueryPort.findCancelsByItemIds(orderItemIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<OrderClaimResult>> findClaimsByItemIds(List<Long> orderItemIds) {
        return compositionQueryPort.findClaimsByItemIds(orderItemIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, OrderItemResult> findOrderItemsByIds(List<Long> orderItemIds) {
        return compositionQueryPort.findOrderItemsByIds(orderItemIds);
    }

    // ==================== V5 상품주문 상세 ====================

    @Transactional(readOnly = true)
    public Optional<ProductOrderDetailData> findProductOrderDetail(long orderItemId) {
        return compositionQueryPort.findProductOrderDetail(orderItemId);
    }

    @Transactional(readOnly = true)
    public List<OrderCancelResult> findCancelsByOrderItemId(long orderItemId) {
        return compositionQueryPort.findCancelsByOrderItemId(orderItemId);
    }

    @Transactional(readOnly = true)
    public List<OrderClaimResult> findClaimsByOrderItemId(long orderItemId) {
        return compositionQueryPort.findClaimsByOrderItemId(orderItemId);
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryResult> findHistoriesByOrderId(String orderId) {
        return compositionQueryPort.findHistoriesByOrderId(orderId);
    }
}
