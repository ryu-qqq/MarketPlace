package com.ryuqq.marketplace.application.legacy.product.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdatePriceCommand;
import com.ryuqq.marketplace.application.legacy.product.port.in.command.LegacyProductUpdatePriceUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductGroupCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.shared.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 가격 수정 서비스.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Service
public class LegacyProductUpdatePriceService implements LegacyProductUpdatePriceUseCase {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdatePriceService(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyProductGroupCommandCoordinator productGroupCommandCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(LegacyUpdatePriceCommand command) {
        StatusChangeContext<LegacyProductGroupId> context =
                commandFactory.createPriceUpdateContext(command);

        productGroupCommandCoordinator.updatePrice(
                context.id(), command.regularPrice(), command.currentPrice(), context.changedAt());

        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
