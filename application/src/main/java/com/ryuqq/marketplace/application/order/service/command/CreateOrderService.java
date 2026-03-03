package com.ryuqq.marketplace.application.order.service.command;

import com.ryuqq.marketplace.application.order.dto.command.CreateOrderCommand;
import com.ryuqq.marketplace.application.order.factory.OrderCommandFactory;
import com.ryuqq.marketplace.application.order.manager.OrderCommandManager;
import com.ryuqq.marketplace.application.order.port.in.command.CreateOrderUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import org.springframework.stereotype.Service;

/** 주문 생성 Service. */
@Service
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderCommandFactory commandFactory;
    private final OrderCommandManager commandManager;

    public CreateOrderService(
            OrderCommandFactory commandFactory, OrderCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public String execute(CreateOrderCommand command) {
        Order order = commandFactory.createOrder(command);
        commandManager.persist(order);
        return order.idValue();
    }
}
