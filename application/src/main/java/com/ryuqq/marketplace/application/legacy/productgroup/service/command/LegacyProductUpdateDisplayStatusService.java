package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductGroupCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductUpdateDisplayStatusUseCase;
import com.ryuqq.marketplace.application.legacy.shared.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
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
    private final LegacyProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateDisplayStatusService(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyProductGroupCommandCoordinator productGroupCommandCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(LegacyUpdateDisplayStatusCommand command) {
        StatusChangeContext<LegacyProductGroupId> context =
                commandFactory.createDisplayStatusChangeContext(command);

        productGroupCommandCoordinator.updateDisplayStatus(
                context.id(), command.displayYn(), context.changedAt());

        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
