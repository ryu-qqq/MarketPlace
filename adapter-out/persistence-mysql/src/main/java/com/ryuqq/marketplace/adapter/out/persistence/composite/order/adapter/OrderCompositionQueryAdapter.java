package com.ryuqq.marketplace.adapter.out.persistence.composite.order.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderListProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.mapper.OrderCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.repository.OrderCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.port.out.query.OrderCompositionQueryPort;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import java.util.List;
import java.util.Optional;
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
