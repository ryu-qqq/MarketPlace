package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.order.mapper.OrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.PaymentJpaRepository;
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.order.port.out.command.OrderCommandPort;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import java.util.List;
import org.springframework.stereotype.Component;

/** Order Command Adapter. */
@Component
public class OrderCommandAdapter implements OrderCommandPort {

    private final OrderJpaRepository orderRepository;
    private final OrderItemJpaRepository itemRepository;
    private final OrderItemHistoryJpaRepository itemHistoryRepository;
    private final PaymentJpaRepository paymentRepository;
    private final OrderJpaEntityMapper mapper;
    private final IdGeneratorPort idGeneratorPort;

    public OrderCommandAdapter(
            OrderJpaRepository orderRepository,
            OrderItemJpaRepository itemRepository,
            OrderItemHistoryJpaRepository itemHistoryRepository,
            PaymentJpaRepository paymentRepository,
            OrderJpaEntityMapper mapper,
            IdGeneratorPort idGeneratorPort) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.itemHistoryRepository = itemHistoryRepository;
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
        this.idGeneratorPort = idGeneratorPort;
    }

    @Override
    public void persist(Order order) {
        orderRepository.save(mapper.toOrderEntity(order));
        paymentRepository.save(mapper.toPaymentEntity(order, idGeneratorPort.generate()));
        itemRepository.saveAll(mapper.toOrderItemEntities(order.items(), order.idValue()));
        // Order 내 모든 OrderItem의 histories를 저장
        itemHistoryRepository.saveAll(mapper.collectAllItemHistoryEntities(order));
    }

    @Override
    public void persistAll(List<Order> orders) {
        orders.forEach(this::persist);
    }
}
