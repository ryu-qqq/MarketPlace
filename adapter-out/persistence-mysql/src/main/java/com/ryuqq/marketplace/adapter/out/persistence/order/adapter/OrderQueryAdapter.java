package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.mapper.OrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderQueryDslRepository;
import com.ryuqq.marketplace.application.order.port.out.query.OrderQueryPort;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Order Query Adapter.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 */
@Component
public class OrderQueryAdapter implements OrderQueryPort {

    private final OrderQueryDslRepository queryDslRepository;
    private final OrderJpaEntityMapper mapper;

    public OrderQueryAdapter(
            OrderQueryDslRepository queryDslRepository, OrderJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return queryDslRepository.findById(id.value()).map(this::toDomainWithRelations);
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return queryDslRepository.findByOrderNumber(orderNumber).map(this::toDomainWithRelations);
    }

    @Override
    public boolean existsByExternalOrderNo(long salesChannelId, String externalOrderNo) {
        return queryDslRepository.existsByExternalOrderNo(salesChannelId, externalOrderNo);
    }

    @Override
    public List<Order> findByCriteria(OrderSearchCriteria criteria) {
        throw new UnsupportedOperationException(
                "findByCriteria는 OrderCompositionQueryPort를 사용하세요.");
    }

    @Override
    public long countByCriteria(OrderSearchCriteria criteria) {
        throw new UnsupportedOperationException(
                "countByCriteria는 OrderCompositionQueryPort를 사용하세요.");
    }

    @Override
    public Map<OrderStatus, Long> countByStatus() {
        return queryDslRepository.countByStatus();
    }

    private Order toDomainWithRelations(OrderJpaEntity orderEntity) {
        String orderId = orderEntity.getId();
        PaymentJpaEntity payment = queryDslRepository.findPaymentByOrderId(orderId).orElse(null);
        List<OrderItemJpaEntity> items = queryDslRepository.findItemsByOrderId(orderId);
        List<OrderHistoryJpaEntity> histories = queryDslRepository.findHistoriesByOrderId(orderId);
        return mapper.toDomain(orderEntity, payment, items, histories);
    }
}
