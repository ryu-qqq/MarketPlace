package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.order.dto.command.StartClaimCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory.OrderStatusChangeWithReasonContext;
import com.ryuqq.marketplace.application.order.manager.OrderCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.StartClaimUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import org.springframework.stereotype.Service;

/**
 * 클레임 접수 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 */
@Service
public class StartClaimService implements StartClaimUseCase {

    private final OrderReadManager readManager;
    private final OrderCommandManager commandManager;
    private final OrderCommandFactory commandFactory;

    public StartClaimService(
            OrderReadManager readManager,
            OrderCommandManager commandManager,
            OrderCommandFactory commandFactory) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
    }

    @Override
    public void execute(StartClaimCommand command) {
        OrderStatusChangeWithReasonContext ctx =
                commandFactory.createStatusContextWithReason(
                        command.orderId(), command.reason(), command.changedBy());
        Order order = readManager.getById(ctx.orderId());
        order.startClaim(ctx.changedBy(), ctx.reason(), ctx.changedAt());
        commandManager.persist(order);
    }
}
