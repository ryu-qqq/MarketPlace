package com.ryuqq.marketplace.application.legacy.product.service.command;

import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdateProductsCommand;
import com.ryuqq.marketplace.application.legacy.product.internal.LegacyOptionUpdateCoordinator;
import com.ryuqq.marketplace.application.legacy.product.port.in.command.LegacyProductUpdateOptionsUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 옵션/상품 수정 Service.
 *
 * <p>LegacyOptionUpdateCoordinator에 위임하여 diff 기반 옵션 업데이트를 수행합니다.
 */
@Service
public class LegacyProductUpdateOptionsService implements LegacyProductUpdateOptionsUseCase {

    private final LegacyOptionUpdateCoordinator optionUpdateCoordinator;
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateOptionsService(
            LegacyOptionUpdateCoordinator optionUpdateCoordinator,
            LegacyProductQueryUseCase legacyProductQueryUseCase,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.optionUpdateCoordinator = optionUpdateCoordinator;
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public LegacyProductGroupDetailResult execute(LegacyUpdateProductsCommand command) {
        optionUpdateCoordinator.execute(command);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
        return legacyProductQueryUseCase.execute(command.productGroupId());
    }
}
