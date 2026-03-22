package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductGroupCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductMarkOutOfStockUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** 레거시 상품 품절 처리 서비스. */
@Service
public class LegacyProductMarkOutOfStockService implements LegacyProductMarkOutOfStockUseCase {

    private final LegacyProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductMarkOutOfStockService(
            LegacyProductGroupCommandCoordinator productGroupCommandCoordinator,
            LegacyProductQueryUseCase legacyProductQueryUseCase,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public LegacyProductGroupDetailResult execute(LegacyMarkOutOfStockCommand command) {
        productGroupCommandCoordinator.markSoldOut(command.productGroupId());
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
        return legacyProductQueryUseCase.execute(command.productGroupId());
    }
}
