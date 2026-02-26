package com.ryuqq.marketplace.application.legacy.product.service.command;

import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdateStockCommand;
import com.ryuqq.marketplace.application.legacy.product.internal.LegacyStockUpdateCoordinator;
import com.ryuqq.marketplace.application.legacy.product.port.in.command.LegacyProductUpdateStockUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 재고 수정 Service.
 *
 * <p>재고 커맨드를 productId → stockQuantity 맵으로 변환 후 LegacyStockUpdateCoordinator에 위임합니다.
 */
@Service
public class LegacyProductUpdateStockService implements LegacyProductUpdateStockUseCase {

    private final LegacyStockUpdateCoordinator stockUpdateCoordinator;
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateStockService(
            LegacyStockUpdateCoordinator stockUpdateCoordinator,
            LegacyProductQueryUseCase legacyProductQueryUseCase,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.stockUpdateCoordinator = stockUpdateCoordinator;
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public LegacyProductGroupDetailResult execute(LegacyUpdateStockCommand command) {
        LegacyProductGroupId groupId = LegacyProductGroupId.of(command.productGroupId());

        Map<Long, Integer> stockUpdates =
                command.stockEntries().stream()
                        .collect(
                                Collectors.toMap(
                                        LegacyUpdateStockCommand.StockEntry::productId,
                                        LegacyUpdateStockCommand.StockEntry::stockQuantity));

        stockUpdateCoordinator.execute(groupId, stockUpdates);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
        return legacyProductQueryUseCase.execute(command.productGroupId());
    }
}
