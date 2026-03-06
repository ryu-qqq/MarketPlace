package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.order.dto.command.PrepareOrderCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.manager.OrderCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.PrepareOrderUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import org.springframework.stereotype.Service;

/**
 * 주문 발주확인 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 */
@Service
public class PrepareOrderService implements PrepareOrderUseCase {

    private final OrderReadManager readManager;
    private final OrderCommandManager commandManager;
    private final OrderCommandFactory commandFactory;

    public PrepareOrderService(
            OrderReadManager readManager,
            OrderCommandManager commandManager,
            OrderCommandFactory commandFactory) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
    }

    @Override
    public void execute(PrepareOrderCommand command) {
        StatusChangeContext<OrderId> ctx = commandFactory.createStatusContext(command.orderId());
        Order order = readManager.getById(ctx.id());
        order.prepare(command.changedBy(), ctx.changedAt());
        commandManager.persist(order);
    }
}
