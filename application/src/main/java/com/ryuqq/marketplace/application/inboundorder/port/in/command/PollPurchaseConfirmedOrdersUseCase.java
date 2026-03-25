package com.ryuqq.marketplace.application.inboundorder.port.in.command;

/**
 * 구매확정 폴링 UseCase.
 *
 * <p>외부 판매채널에서 구매확정된 상품주문을 주기적으로 폴링하여 내부 상태를 갱신합니다.
 */
public interface PollPurchaseConfirmedOrdersUseCase {

    void execute(long salesChannelId);
}
