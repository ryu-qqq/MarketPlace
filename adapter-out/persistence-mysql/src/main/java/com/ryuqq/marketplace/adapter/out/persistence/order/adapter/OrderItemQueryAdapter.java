package com.ryuqq.marketplace.adapter.out.persistence.order.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.order.mapper.OrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.application.order.port.out.query.OrderItemQueryPort;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
    public List<OrderItem> findAllByIds(List<OrderItemId> orderItemIds) {
        List<String> ids = orderItemIds.stream().map(OrderItemId::value).toList();
        return itemRepository.findAllById(ids).stream().map(mapper::toOrderItem).toList();
    }

    @Override
    public Map<OrderItemStatus, Long> countByStatus() {
        List<Object[]> rows = itemRepository.countGroupByStatus();
        Map<OrderItemStatus, Long> result = new EnumMap<>(OrderItemStatus.class);
        for (Object[] row : rows) {
            String statusName = (String) row[0];
            Long count = (Long) row[1];
            try {
                OrderItemStatus status = OrderItemStatus.valueOf(statusName);
                result.put(status, count);
            } catch (IllegalArgumentException ignored) {
                // 알 수 없는 상태 무시
            }
        }
        return result;
    }
}
