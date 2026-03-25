package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.ConfirmOrderUseCase;
import com.ryuqq.marketplace.application.order.internal.OrderSettlementProcessor;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 주문상품 구매 확정 Service.
 *
 * <p>대상 주문상품을 조회하여 CONFIRMED 상태로 전환합니다.
 */
@Service
public class ConfirmOrderService implements ConfirmOrderUseCase {

    private final OrderCommandFactory factory;
    private final OrderItemReadManager readManager;
    private final OrderItemCommandManager commandManager;
    private final OrderSettlementProcessor orderSettlementProcessor;

    public ConfirmOrderService(
            OrderCommandFactory factory,
            OrderItemReadManager readManager,
            OrderItemCommandManager commandManager,
            OrderSettlementProcessor orderSettlementProcessor) {
        this.factory = factory;
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.orderSettlementProcessor = orderSettlementProcessor;
    }

    @Override
    public void execute(OrderItemStatusCommand command) {
        StatusChangeContext<List<OrderItemId>> ctx = factory.createStatusChangeContext(command);
        List<OrderItem> orderItems = readManager.findAllByIds(ctx.id());
        orderItems.forEach(item -> item.confirm(command.changedBy(), ctx.changedAt()));
        commandManager.persistAll(orderItems);

        for (OrderItem item : orderItems) {
            orderSettlementProcessor.createSalesEntry(
                    item.idValue(), item.sellerId(), item.price().paymentAmount().value());
        }
    }
}
