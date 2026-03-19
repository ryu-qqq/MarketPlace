package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.CompleteExchangeUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 교환 완료 처리 Service.
 *
 * <p>대상 주문상품을 조회하여 반품 완료(RETURNED) 상태로 전환합니다.
 */
@Service
public class CompleteExchangeService implements CompleteExchangeUseCase {

    private final OrderCommandFactory factory;
    private final OrderItemReadManager readManager;
    private final OrderItemCommandManager commandManager;

    public CompleteExchangeService(
            OrderCommandFactory factory,
            OrderItemReadManager readManager,
            OrderItemCommandManager commandManager) {
        this.factory = factory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(OrderItemStatusCommand command) {
        StatusChangeContext<List<OrderItemId>> ctx = factory.createStatusChangeContext(command);
        List<OrderItem> orderItems = readManager.findAllByIds(ctx.id());
        orderItems.forEach(item -> item.completeReturn(command.changedBy(), ctx.changedAt()));
        commandManager.persistAll(orderItems);
    }
}
