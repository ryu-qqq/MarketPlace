package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductGroupCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductUpdateDisplayStatusUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** 레거시 상품 전시 상태 변경 서비스. */
@Service
public class LegacyProductUpdateDisplayStatusService
        implements LegacyProductUpdateDisplayStatusUseCase {

    private final LegacyProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateDisplayStatusService(
            LegacyProductGroupCommandCoordinator productGroupCommandCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(LegacyUpdateDisplayStatusCommand command) {
        productGroupCommandCoordinator.updateDisplayYn(
                command.productGroupId(), command.displayYn());
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
