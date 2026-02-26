package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.application.legacyproduct.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupReadManager;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateDisplayStatusUseCase;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 전시 상태 변경 서비스.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Service
public class LegacyProductUpdateDisplayStatusService
        implements LegacyProductUpdateDisplayStatusUseCase {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyProductGroupReadManager readManager;
    private final LegacyProductGroupCommandManager commandManager;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateDisplayStatusService(
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
    public void execute(LegacyUpdateDisplayStatusCommand command) {
        StatusChangeContext<LegacyProductGroupId> context =
                commandFactory.createDisplayStatusChangeContext(command);

        LegacyProductGroup productGroup = readManager.getById(context.id());
        productGroup.updateDisplayYn(command.displayYn(), context.changedAt());

        commandManager.persist(productGroup);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
