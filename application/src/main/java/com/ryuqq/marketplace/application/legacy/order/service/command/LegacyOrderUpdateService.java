package com.ryuqq.marketplace.application.legacy.order.service.command;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderUpdateResult;
import com.ryuqq.marketplace.application.legacy.order.internal.LegacyOrderMarketRouter;
import com.ryuqq.marketplace.application.legacy.order.port.in.command.LegacyOrderUpdateUseCase;
import com.ryuqq.marketplace.application.legacy.order.resolver.LegacyOrderIdResolver;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 주문 상태 변경 서비스.
 *
 * <p>market 스키마의 표준 UseCase로 라우팅합니다.
 */
@Service
public class LegacyOrderUpdateService implements LegacyOrderUpdateUseCase {

    private final LegacyOrderIdResolver idResolver;
    private final LegacyOrderMarketRouter marketRouter;

    public LegacyOrderUpdateService(
            LegacyOrderIdResolver idResolver,
            LegacyOrderMarketRouter marketRouter) {
        this.idResolver = idResolver;
        this.marketRouter = marketRouter;
    }

    @Override
    @Transactional
    public LegacyOrderUpdateResult execute(LegacyOrderUpdateCommand command) {
        LegacyOrderIdMapping mapping = idResolver
                .resolve(command.orderId())
                .orElseThrow(() -> new com.ryuqq.marketplace.domain.order.exception.OrderNotFoundException(
                        String.valueOf(command.orderId())));

        marketRouter.route(command, mapping);

        return new LegacyOrderUpdateResult(
                command.orderId(),
                0L,
                "",
                command.orderStatus(),
                command.changeReason(),
                command.changeDetailReason());
    }
}
