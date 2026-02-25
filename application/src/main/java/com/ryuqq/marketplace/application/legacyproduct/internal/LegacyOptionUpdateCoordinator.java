package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductsCommand;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyProductIdResolver.ResolvedLegacyProductId;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductsUseCase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 상품 옵션/상품 수정 Coordinator. */
@Component
public class LegacyOptionUpdateCoordinator extends LegacyProductUpdateCoordinator {

    private final UpdateProductsUseCase updateProductsUseCase;

    public LegacyOptionUpdateCoordinator(
            LegacyProductIdResolver idResolver, UpdateProductsUseCase updateProductsUseCase) {
        super(idResolver);
        this.updateProductsUseCase = updateProductsUseCase;
    }

    @Transactional
    public void execute(LegacyUpdateProductsCommand command) {
        ResolvedLegacyProductId resolved = idResolver.resolve(command.setofProductGroupId());
        long internalId = resolved.internalProductGroupId();

        UpdateProductsCommand inner = command.command();
        UpdateProductsCommand resolvedCommand =
                new UpdateProductsCommand(internalId, inner.optionGroups(), inner.products());
        updateProductsUseCase.execute(resolvedCommand);
    }
}
