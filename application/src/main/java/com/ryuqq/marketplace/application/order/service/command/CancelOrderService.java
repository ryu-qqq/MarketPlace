package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.order.dto.command.CancelOrderCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory.OrderStatusChangeWithReasonContext;
import com.ryuqq.marketplace.application.order.manager.OrderCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.CancelOrderUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import org.springframework.stereotype.Service;

/**
 * 주문 취소 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 */
@Service
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderReadManager readManager;
    private final OrderCommandManager commandManager;
    private final OrderCommandFactory commandFactory;

    public CancelOrderService(
            OrderReadManager readManager,
            OrderCommandManager commandManager,
            OrderCommandFactory commandFactory) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
    }

    @Override
    public void execute(CancelOrderCommand command) {
        OrderStatusChangeWithReasonContext ctx =
                commandFactory.createStatusContextWithReason(
                        command.orderId(), command.reason(), command.changedBy());
        Order order = readManager.getById(ctx.orderId());
        order.cancel(ctx.changedBy(), ctx.reason(), ctx.changedAt());
        commandManager.persist(order);
    }
}
