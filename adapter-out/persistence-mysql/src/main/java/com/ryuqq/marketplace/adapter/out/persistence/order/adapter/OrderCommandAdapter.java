package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.order.mapper.OrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.application.order.port.out.command.OrderCommandPort;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import java.util.List;
import org.springframework.stereotype.Component;

/** Order Command Adapter. */
@Component
public class OrderCommandAdapter implements OrderCommandPort {

    private final OrderJpaRepository orderRepository;
    private final OrderItemJpaRepository itemRepository;
    private final OrderHistoryJpaRepository historyRepository;
    private final OrderJpaEntityMapper mapper;

    public OrderCommandAdapter(
            OrderJpaRepository orderRepository,
            OrderItemJpaRepository itemRepository,
            OrderHistoryJpaRepository historyRepository,
            OrderJpaEntityMapper mapper) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.historyRepository = historyRepository;
        this.mapper = mapper;
    }

    @Override
    public void persist(Order order) {
        orderRepository.save(mapper.toOrderEntity(order));
        itemRepository.saveAll(mapper.toOrderItemEntities(order.items(), order.idValue()));
        historyRepository.saveAll(mapper.toOrderHistoryEntities(order.histories()));
    }

    @Override
    public void persistAll(List<Order> orders) {
        orders.forEach(this::persist);
    }
}
