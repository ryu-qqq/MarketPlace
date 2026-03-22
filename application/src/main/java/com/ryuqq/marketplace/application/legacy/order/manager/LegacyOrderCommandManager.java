package com.ryuqq.marketplace.application.legacy.order.manager;

import com.ryuqq.marketplace.application.legacy.order.port.out.command.LegacyOrderCommandPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 주문 커맨드 매니저.
 *
 * <p>LegacyOrderCommandPort 호출 래퍼.
 */
@Component
@Transactional
public class LegacyOrderCommandManager {

    private final LegacyOrderCommandPort commandPort;

    public LegacyOrderCommandManager(LegacyOrderCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    public void updateOrderStatus(long orderId, String orderStatus) {
        commandPort.updateOrderStatus(orderId, orderStatus);
    }

    public void insertOrderHistory(
            long orderId, String orderStatus, String changeReason, String changeDetailReason) {
        commandPort.insertOrderHistory(orderId, orderStatus, changeReason, changeDetailReason);
    }
}
