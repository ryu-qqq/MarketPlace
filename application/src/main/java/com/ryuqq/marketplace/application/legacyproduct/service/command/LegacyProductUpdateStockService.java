package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateStockCommand;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyStockUpdateCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateStockUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import org.springframework.stereotype.Service;

/** 레거시 상품 재고 수정 Service. */
@Service
public class LegacyProductUpdateStockService implements LegacyProductUpdateStockUseCase {

    private final LegacyStockUpdateCoordinator stockUpdateCoordinator;
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;

    public LegacyProductUpdateStockService(
            LegacyStockUpdateCoordinator stockUpdateCoordinator,
            LegacyProductQueryUseCase legacyProductQueryUseCase) {
        this.stockUpdateCoordinator = stockUpdateCoordinator;
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
    }

    @Override
    public ProductGroupDetailCompositeResult execute(LegacyUpdateStockCommand command) {
        stockUpdateCoordinator.execute(command.commands());
        return legacyProductQueryUseCase.execute(command.setofProductGroupId());
    }
}
