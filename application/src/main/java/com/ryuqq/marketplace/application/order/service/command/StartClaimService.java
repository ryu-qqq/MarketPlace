package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.order.dto.command.StartClaimCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.StartClaimUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 클레임 시작(반품 요청) Service.
 *
 * <p>대상 주문상품을 조회하여 RETURN_REQUESTED 상태로 전환합니다.
 */
@Service
public class StartClaimService implements StartClaimUseCase {

    private final OrderCommandFactory factory;
    private final OrderItemReadManager readManager;
    private final OrderItemCommandManager commandManager;

    public StartClaimService(
            OrderCommandFactory factory,
            OrderItemReadManager readManager,
            OrderItemCommandManager commandManager) {
        this.factory = factory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(StartClaimCommand command) {
        StatusChangeContext<List<OrderItemId>> ctx = factory.createClaimContext(command);
        List<OrderItem> orderItems = readManager.findAllByIds(ctx.id());
        orderItems.forEach(
                item -> item.requestReturn(command.changedBy(), command.reason(), ctx.changedAt()));
        commandManager.persistAll(orderItems);
    }
}
