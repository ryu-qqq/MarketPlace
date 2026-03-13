package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.order.mapper.OrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.application.order.port.out.query.OrderItemQueryPort;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;

/** OrderItem Query Adapter. 주문상품 도메인 객체 조회를 위한 전용 어댑터. */
@Component
public class OrderItemQueryAdapter implements OrderItemQueryPort {

    private final OrderItemJpaRepository itemRepository;
    private final OrderJpaEntityMapper mapper;

    public OrderItemQueryAdapter(
            OrderItemJpaRepository itemRepository, OrderJpaEntityMapper mapper) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @Override
    public List<OrderItem> findAllByIds(List<String> orderItemIds) {
        return itemRepository.findAllById(orderItemIds).stream().map(mapper::toOrderItem).toList();
    }
}
