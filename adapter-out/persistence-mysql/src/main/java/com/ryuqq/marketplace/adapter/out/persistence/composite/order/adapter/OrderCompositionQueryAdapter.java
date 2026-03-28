package com.ryuqq.marketplace.adapter.out.persistence.composite.order.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderListProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.mapper.OrderCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.repository.OrderCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailData;
import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.port.out.query.OrderCompositionQueryPort;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** 주문 Composition 조회 Adapter. 크로스 테이블 조인을 통한 성능 최적화된 조회 구현. */
@Component
public class OrderCompositionQueryAdapter implements OrderCompositionQueryPort {

    private final OrderCompositeQueryDslRepository compositeRepository;
    private final OrderCompositeMapper compositeMapper;

    public OrderCompositionQueryAdapter(
            OrderCompositeQueryDslRepository compositeRepository,
            OrderCompositeMapper compositeMapper) {
        this.compositeRepository = compositeRepository;
        this.compositeMapper = compositeMapper;
    }

    @Override
    public List<OrderListResult> searchOrders(OrderSearchCriteria criteria) {
        return compositeRepository.searchOrders(criteria).stream()
                .map(compositeMapper::toListResult)
                .toList();
    }

    @Override
    public long countOrders(OrderSearchCriteria criteria) {
        return compositeRepository.countOrders(criteria);
    }

    @Override
    public Optional<OrderDetailResult> findOrderDetail(String orderId) {
        return compositeRepository
                .findOrderWithPayment(orderId)
                .map(order -> buildDetailResult(orderId, order));
    }

    // ==================== V5 상품주문 리스트 ====================

    @Override
    public List<OrderItemResult> searchProductOrders(OrderSearchCriteria criteria) {
        return compositeRepository.searchProductOrders(criteria).stream()
                .map(compositeMapper::toItemResultFromProjection)
                .toList();
    }

    @Override
    public long countProductOrders(OrderSearchCriteria criteria) {
        return compositeRepository.countProductOrders(criteria);
    }

    @Override
    public Map<String, OrderListResult> findOrdersByIds(List<String> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Map.of();
        }
        return compositeRepository.findOrdersByIds(orderIds).stream()
                .map(compositeMapper::toListResult)
                .collect(Collectors.toMap(OrderListResult::orderId, Function.identity()));
    }

    @Override
    public Map<Long, List<OrderCancelResult>> findCancelsByItemIds(List<Long> orderItemIds) {
        if (orderItemIds == null || orderItemIds.isEmpty()) {
            return Map.of();
        }
        return compositeRepository.findCancelsByOrderItemIds(orderItemIds).stream()
                .map(compositeMapper::toCancelResult)
                .collect(Collectors.groupingBy(OrderCancelResult::orderItemId));
    }

    @Override
    public Map<Long, List<OrderClaimResult>> findClaimsByItemIds(List<Long> orderItemIds) {
        if (orderItemIds == null || orderItemIds.isEmpty()) {
            return Map.of();
        }
        return compositeRepository.findClaimsByOrderItemIds(orderItemIds).stream()
                .map(compositeMapper::toClaimResult)
                .collect(Collectors.groupingBy(OrderClaimResult::orderItemId));
    }

    @Override
    public Map<Long, OrderItemResult> findOrderItemsByIds(List<Long> orderItemIds) {
        if (orderItemIds == null || orderItemIds.isEmpty()) {
            return Map.of();
        }
        return compositeRepository.findOrderItemsByIds(orderItemIds).stream()
                .map(compositeMapper::toItemResultFromProjection)
                .collect(Collectors.toMap(OrderItemResult::orderItemId, Function.identity()));
    }

    // ==================== V5 상품주문 상세 ====================

    @Override
    public Optional<ProductOrderDetailData> findProductOrderDetail(Long orderItemId) {
        return compositeRepository
                .findProductOrderDetail(orderItemId)
                .map(compositeMapper::toDetailData);
    }

    @Override
    public List<OrderCancelResult> findCancelsByOrderItemId(Long orderItemId) {
        return compositeRepository.findCancelsByOrderItemId(orderItemId).stream()
                .map(compositeMapper::toCancelResult)
                .toList();
    }

    @Override
    public List<OrderClaimResult> findClaimsByOrderItemId(Long orderItemId) {
        return compositeRepository.findClaimsByOrderItemId(orderItemId).stream()
                .map(compositeMapper::toClaimResult)
                .toList();
    }

    @Override
    public List<OrderHistoryResult> findHistoriesByOrderId(String orderId) {
        return compositeRepository.findOrderHistories(orderId).stream()
                .map(compositeMapper::toHistoryResult)
                .toList();
    }

    private OrderDetailResult buildDetailResult(String orderId, OrderListProjectionDto order) {
        OrderDetailCompositeDto composite =
                new OrderDetailCompositeDto(
                        order,
                        compositeRepository.findOrderItems(orderId),
                        compositeRepository.findOrderHistories(orderId),
                        compositeRepository.findOrderCancels(orderId),
                        compositeRepository.findOrderClaims(orderId));
        return compositeMapper.toDetailResult(composite);
    }
}
