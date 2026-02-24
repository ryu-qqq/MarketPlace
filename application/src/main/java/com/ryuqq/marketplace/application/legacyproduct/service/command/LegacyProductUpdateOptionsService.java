package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductsCommand;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyOptionUpdateCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateOptionsUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import org.springframework.stereotype.Service;

/** 레거시 상품 옵션/상품 수정 Service. */
@Service
public class LegacyProductUpdateOptionsService implements LegacyProductUpdateOptionsUseCase {

    private final LegacyOptionUpdateCoordinator optionUpdateCoordinator;
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;

    public LegacyProductUpdateOptionsService(
            LegacyOptionUpdateCoordinator optionUpdateCoordinator,
            LegacyProductQueryUseCase legacyProductQueryUseCase) {
        this.optionUpdateCoordinator = optionUpdateCoordinator;
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
    }

    @Override
    public ProductGroupDetailCompositeResult execute(LegacyUpdateProductsCommand command) {
        optionUpdateCoordinator.execute(command);
        return legacyProductQueryUseCase.execute(command.setofProductGroupId());
    }
}
