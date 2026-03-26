package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductGroupCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductUpdatePriceUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** 레거시 상품 가격 수정 서비스. */
@Service
public class LegacyProductUpdatePriceService implements LegacyProductUpdatePriceUseCase {

    private final LegacyProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdatePriceService(
            LegacyProductGroupCommandCoordinator productGroupCommandCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(long productGroupId, long regularPrice, long currentPrice) {
        productGroupCommandCoordinator.updatePrice(productGroupId, regularPrice, currentPrice);
        conversionOutboxCommandManager.createIfNoPending(productGroupId, Instant.now());
    }
}
