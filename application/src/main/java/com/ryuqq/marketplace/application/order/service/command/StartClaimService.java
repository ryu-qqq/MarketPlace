package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.dto.command.StartClaimCommand;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.order.port.in.command.StartClaimUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 클레임 시작(반품 요청) Service.
 *
 * <p>대상 주문상품을 조회하여 RETURN_REQUESTED 상태로 전환합니다.
 */
@Service
public class StartClaimService implements StartClaimUseCase {

    private final OrderItemReadManager readManager;
    private final OrderItemCommandManager commandManager;
    private final TimeProvider timeProvider;

    public StartClaimService(
            OrderItemReadManager readManager,
            OrderItemCommandManager commandManager,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public void execute(StartClaimCommand command) {
        Instant now = timeProvider.now();
        List<OrderItem> orderItems = readManager.findAllByIds(command.orderItemIds());
        orderItems.forEach(item -> item.requestReturn(command.changedBy(), command.reason(), now));
        commandManager.updateAll(orderItems);
    }
}
