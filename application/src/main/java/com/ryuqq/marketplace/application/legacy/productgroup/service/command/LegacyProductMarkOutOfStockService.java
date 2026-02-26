package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductGroupCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductMarkOutOfStockUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacy.shared.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 품절 처리 서비스.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Service
public class LegacyProductMarkOutOfStockService implements LegacyProductMarkOutOfStockUseCase {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductMarkOutOfStockService(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyProductGroupCommandCoordinator productGroupCommandCoordinator,
            LegacyProductQueryUseCase legacyProductQueryUseCase,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public LegacyProductGroupDetailResult execute(LegacyMarkOutOfStockCommand command) {
        StatusChangeContext<LegacyProductGroupId> context =
                commandFactory.createMarkOutOfStockContext(command);

        productGroupCommandCoordinator.markSoldOut(context.id(), context.changedAt());

        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
        return legacyProductQueryUseCase.execute(command.productGroupId());
    }
}
