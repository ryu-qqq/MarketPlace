package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.order.dto.command.CompleteExchangeCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.manager.OrderCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.CompleteExchangeUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import org.springframework.stereotype.Service;

/**
 * 교환 완료 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 */
@Service
public class CompleteExchangeService implements CompleteExchangeUseCase {

    private final OrderReadManager readManager;
    private final OrderCommandManager commandManager;
    private final OrderCommandFactory commandFactory;

    public CompleteExchangeService(
            OrderReadManager readManager,
            OrderCommandManager commandManager,
            OrderCommandFactory commandFactory) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
    }

    @Override
    public void execute(CompleteExchangeCommand command) {
        StatusChangeContext<OrderId> ctx = commandFactory.createStatusContext(command.orderId());
        Order order = readManager.getById(ctx.id());
        order.completeExchange(command.changedBy(), ctx.changedAt());
        commandManager.persist(order);
    }
}
