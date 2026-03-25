package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.ConfirmOrderUseCase;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateSalesEntryCommand;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.CreateSalesEntryUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 주문상품 구매 확정 Service.
 *
 * <p>대상 주문상품을 조회하여 CONFIRMED 상태로 전환합니다.
 */
@Service
public class ConfirmOrderService implements ConfirmOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConfirmOrderService.class);
    private static final int DEFAULT_COMMISSION_RATE = 0;

    private final OrderCommandFactory factory;
    private final OrderItemReadManager readManager;
    private final OrderItemCommandManager commandManager;
    private final CreateSalesEntryUseCase createSalesEntryUseCase;

    public ConfirmOrderService(
            OrderCommandFactory factory,
            OrderItemReadManager readManager,
            OrderItemCommandManager commandManager,
            CreateSalesEntryUseCase createSalesEntryUseCase) {
        this.factory = factory;
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.createSalesEntryUseCase = createSalesEntryUseCase;
    }

    @Override
    public void execute(OrderItemStatusCommand command) {
        StatusChangeContext<List<OrderItemId>> ctx = factory.createStatusChangeContext(command);
        List<OrderItem> orderItems = readManager.findAllByIds(ctx.id());
        orderItems.forEach(item -> item.confirm(command.changedBy(), ctx.changedAt()));
        commandManager.persistAll(orderItems);

        for (OrderItem item : orderItems) {
            try {
                createSalesEntryUseCase.execute(
                        new CreateSalesEntryCommand(
                                item.idValue(),
                                item.sellerId(),
                                item.price().paymentAmount().value(),
                                DEFAULT_COMMISSION_RATE));
            } catch (Exception e) {
                log.warn(
                        "정산 Entry 생성 실패: orderItemId={}, error={}",
                        item.idValue(),
                        e.getMessage());
            }
        }
    }
}
