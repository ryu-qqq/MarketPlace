package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdatePriceCommand;
import com.ryuqq.marketplace.application.legacyproduct.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupReadManager;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdatePriceUseCase;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
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
    private final LegacyProductGroupReadManager readManager;
    private final LegacyProductGroupCommandManager commandManager;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdatePriceService(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyProductGroupReadManager readManager,
            LegacyProductGroupCommandManager commandManager,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(LegacyUpdatePriceCommand command) {
        StatusChangeContext<LegacyProductGroupId> context =
                commandFactory.createPriceUpdateContext(command);

        LegacyProductGroup productGroup = readManager.getById(context.id());
        productGroup.updatePrice(
                command.regularPrice(), command.currentPrice(), context.changedAt());

        commandManager.persist(productGroup);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
