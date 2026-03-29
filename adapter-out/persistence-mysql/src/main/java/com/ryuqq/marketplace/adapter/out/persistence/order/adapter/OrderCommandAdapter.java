package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.mapper.OrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.PaymentJpaRepository;
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.order.port.out.command.OrderCommandPort;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.ArrayList;
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

        List<OrderItemJpaEntity> savedItems =
                itemRepository.saveAll(
                        mapper.toOrderItemEntities(
                                order.items(),
                                order.idValue(),
                                order.createdAt(),
                                order.updatedAt()));

        // auto_increment로 할당된 ID를 도메인에 반영 + history 엔티티 생성
        List<OrderItemHistoryJpaEntity> histories = new ArrayList<>();
        List<OrderItem> domainItems = order.items();
        for (int i = 0; i < savedItems.size(); i++) {
            Long assignedId = savedItems.get(i).getId();
            domainItems.get(i).assignId(OrderItemId.of(assignedId));
            domainItems
                    .get(i)
                    .histories()
                    .forEach(
                            h ->
                                    histories.add(
                                            mapper.toOrderItemHistoryEntityWithId(h, assignedId)));
        }
        itemHistoryRepository.saveAll(histories);
    }

    @Override
    public void persistAll(List<Order> orders) {
        orders.forEach(this::persist);
    }
}
