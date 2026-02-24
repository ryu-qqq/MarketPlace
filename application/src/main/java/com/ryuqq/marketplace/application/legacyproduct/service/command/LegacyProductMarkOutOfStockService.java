package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyOutOfStockCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductMarkOutOfStockUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import org.springframework.stereotype.Service;

/** 레거시 상품 품절 처리 Service. */
@Service
public class LegacyProductMarkOutOfStockService implements LegacyProductMarkOutOfStockUseCase {

    private final LegacyOutOfStockCoordinator outOfStockCoordinator;
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;

    public LegacyProductMarkOutOfStockService(
            LegacyOutOfStockCoordinator outOfStockCoordinator,
            LegacyProductQueryUseCase legacyProductQueryUseCase) {
        this.outOfStockCoordinator = outOfStockCoordinator;
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
    }

    @Override
    public ProductGroupDetailCompositeResult execute(LegacyMarkOutOfStockCommand command) {
        outOfStockCoordinator.execute(command.setofProductGroupId());
        return legacyProductQueryUseCase.execute(command.setofProductGroupId());
    }
}
