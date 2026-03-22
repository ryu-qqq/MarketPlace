package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyOrderEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyOrderHistoryEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyOrderHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyOrderJpaRepository;
import com.ryuqq.marketplace.application.legacy.order.port.out.command.LegacyOrderCommandPort;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 커맨드 Adapter.
 *
 * <p>{@link LegacyOrderCommandPort} 구현체. JPA Repository로 상태 UPDATE + 이력 INSERT.
 */
@Component
public class LegacyOrderApiCommandAdapter implements LegacyOrderCommandPort {

    private final LegacyOrderJpaRepository orderRepository;
    private final LegacyOrderHistoryJpaRepository historyRepository;

    public LegacyOrderApiCommandAdapter(
            LegacyOrderJpaRepository orderRepository,
            LegacyOrderHistoryJpaRepository historyRepository) {
        this.orderRepository = orderRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    public void updateOrderStatus(long orderId, String orderStatus) {
        LegacyOrderEntity order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "레거시 주문을 찾을 수 없습니다. orderId=" + orderId));
        order.updateOrderStatus(orderStatus);
    }

    @Override
    public void insertOrderHistory(
            long orderId, String orderStatus, String changeReason, String changeDetailReason) {
        LegacyOrderHistoryEntity history =
                LegacyOrderHistoryEntity.create(
                        orderId, orderStatus, changeReason, changeDetailReason);
        historyRepository.save(history);
    }
}
