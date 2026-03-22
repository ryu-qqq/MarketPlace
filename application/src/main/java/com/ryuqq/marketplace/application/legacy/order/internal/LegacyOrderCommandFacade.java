package com.ryuqq.marketplace.application.legacy.order.internal;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import com.ryuqq.marketplace.application.legacy.order.manager.LegacyOrderCommandManager;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 상태변경 공통 Facade.
 *
 * <p>모든 전략이 공통으로 수행하는 luxurydb 상태 UPDATE + 이력 INSERT를 캡슐화합니다.
 * 나중에 새 스키마로 전환 시 이 Facade만 교체하면 됩니다.
 */
@Component
public class LegacyOrderCommandFacade {

    private final LegacyOrderCommandManager commandManager;

    public LegacyOrderCommandFacade(LegacyOrderCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void updateStatusAndHistory(LegacyOrderUpdateCommand command) {
        commandManager.updateOrderStatus(command.orderId(), command.orderStatus());
        commandManager.insertOrderHistory(
                command.orderId(),
                command.orderStatus(),
                command.changeReason(),
                command.changeDetailReason());
    }
}
