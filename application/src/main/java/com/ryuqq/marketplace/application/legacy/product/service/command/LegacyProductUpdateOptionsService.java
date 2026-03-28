package com.ryuqq.marketplace.application.legacy.product.service.command;

import com.ryuqq.marketplace.application.legacy.product.port.in.command.LegacyProductUpdateOptionsUseCase;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductsUseCase;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 옵션/SKU 수정 서비스.
 *
 * <p>Factory에서 Command PK resolve 후, 표준 UpdateProductsUseCase에 위임합니다.
 */
@Service
public class LegacyProductUpdateOptionsService implements LegacyProductUpdateOptionsUseCase {

    private final LegacyProductIdResolveFactory resolveFactory;
    private final UpdateProductsUseCase updateProductsUseCase;

    public LegacyProductUpdateOptionsService(
            LegacyProductIdResolveFactory resolveFactory,
            UpdateProductsUseCase updateProductsUseCase) {
        this.resolveFactory = resolveFactory;
        this.updateProductsUseCase = updateProductsUseCase;
    }

    @Override
    public void execute(UpdateProductsCommand command) {
        UpdateProductsCommand resolvedCommand =
                resolveFactory.resolveUpdateProductsCommand(command);
        updateProductsUseCase.execute(resolvedCommand);
    }
}
