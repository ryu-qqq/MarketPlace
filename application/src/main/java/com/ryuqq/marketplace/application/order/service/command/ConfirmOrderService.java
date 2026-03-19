package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.ConfirmOrderUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 주문상품 구매 확정 Service.
 *
 * <p>대상 주문상품을 조회하여 CONFIRMED 상태로 전환합니다.
 */
@Service
public class ConfirmOrderService implements ConfirmOrderUseCase {

    private final OrderItemReadManager readManager;
    private final OrderItemCommandManager commandManager;
    private final TimeProvider timeProvider;

    public ConfirmOrderService(
            OrderItemReadManager readManager,
            OrderItemCommandManager commandManager,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public void execute(OrderItemStatusCommand command) {
        Instant now = timeProvider.now();
        List<OrderItem> orderItems = readManager.findAllByIds(command.orderItemIds());
        orderItems.forEach(item -> item.confirm(command.changedBy(), now));
        commandManager.updateAll(orderItems);
    }
}
