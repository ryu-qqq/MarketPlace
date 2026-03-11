package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.application.order.port.out.command.OrderItemCommandPort;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;

/** OrderItem Command Adapter. 주문상품 상태 변경을 위한 전용 어댑터. */
@Component
public class OrderItemCommandAdapter implements OrderItemCommandPort {

    private final OrderItemJpaRepository itemRepository;

    public OrderItemCommandAdapter(OrderItemJpaRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void updateStatusAll(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            itemRepository
                    .findById(item.idValue())
                    .ifPresent(entity -> entity.updateDeliveryStatus(item.status().name()));
        }
    }
}
