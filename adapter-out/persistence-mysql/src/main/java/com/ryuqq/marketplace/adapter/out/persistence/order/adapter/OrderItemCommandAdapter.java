package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.order.mapper.OrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.application.order.port.out.command.OrderItemCommandPort;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;

/** OrderItem Command Adapter. 주문상품 저장 + 상태 변경 + 이력 저장. */
@Component
public class OrderItemCommandAdapter implements OrderItemCommandPort {

    private final OrderItemJpaRepository itemRepository;
    private final OrderItemHistoryJpaRepository itemHistoryRepository;
    private final OrderJpaEntityMapper mapper;

    public OrderItemCommandAdapter(
            OrderItemJpaRepository itemRepository,
            OrderItemHistoryJpaRepository itemHistoryRepository,
            OrderJpaEntityMapper mapper) {
        this.itemRepository = itemRepository;
        this.itemHistoryRepository = itemHistoryRepository;
        this.mapper = mapper;
    }

    @Override
    public void persistAll(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            itemRepository
                    .findById(item.idValue())
                    .ifPresentOrElse(
                            entity -> entity.updateOrderItemStatus(item.status().name()),
                            () ->
                                    itemRepository.save(
                                            mapper.toOrderItemEntity(item, item.idValue())));
            itemHistoryRepository.saveAll(mapper.toOrderItemHistoryEntities(item.histories()));
        }
    }
}
