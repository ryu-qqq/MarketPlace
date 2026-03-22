package com.ryuqq.marketplace.application.legacy.order.service.command;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderUpdateResult;
import com.ryuqq.marketplace.application.legacy.order.internal.LegacyOrderUpdateStrategy;
import com.ryuqq.marketplace.application.legacy.order.internal.LegacyOrderUpdateStrategyProvider;
import com.ryuqq.marketplace.application.legacy.order.port.in.command.LegacyOrderUpdateUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 주문 상태 변경 서비스.
 *
 * <p>전략 프로바이더에서 orderStatus에 맞는 전략을 O(1)로 조회하여 실행합니다.
 */
@Service
public class LegacyOrderUpdateService implements LegacyOrderUpdateUseCase {

    private final LegacyOrderUpdateStrategyProvider strategyProvider;

    public LegacyOrderUpdateService(LegacyOrderUpdateStrategyProvider strategyProvider) {
        this.strategyProvider = strategyProvider;
    }

    @Override
    @Transactional
    public LegacyOrderUpdateResult execute(LegacyOrderUpdateCommand command) {
        LegacyOrderUpdateStrategy strategy = strategyProvider.getStrategy(command.orderStatus());
        strategy.execute(command);

        return new LegacyOrderUpdateResult(
                command.orderId(),
                0L,
                "",
                command.orderStatus(),
                command.changeReason(),
                command.changeDetailReason());
    }
}
