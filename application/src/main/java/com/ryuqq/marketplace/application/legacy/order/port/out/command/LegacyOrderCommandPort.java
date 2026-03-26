package com.ryuqq.marketplace.application.legacy.order.port.out.command;

/** 레거시 주문 커맨드 Port. */
public interface LegacyOrderCommandPort {

    void updateOrderStatus(long orderId, String orderStatus);

    void insertOrderHistory(
            long orderId, String orderStatus, String changeReason, String changeDetailReason);
}
