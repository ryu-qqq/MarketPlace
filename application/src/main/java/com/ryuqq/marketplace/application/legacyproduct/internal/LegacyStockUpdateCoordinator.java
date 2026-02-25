package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateStockCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductStockUseCase;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 재고 수정 Coordinator.
 *
 * <p>재고 업데이트를 수행합니다. Product-level ID 변환은 legacy_product_id_mapping 구현 시 추가 예정입니다.
 */
@Component
public class LegacyStockUpdateCoordinator {

    private final LegacyProductIdResolver idResolver;
    private final UpdateProductStockUseCase updateProductStockUseCase;

    public LegacyStockUpdateCoordinator(
            LegacyProductIdResolver idResolver,
            UpdateProductStockUseCase updateProductStockUseCase) {
        this.idResolver = idResolver;
        this.updateProductStockUseCase = updateProductStockUseCase;
    }

    @Transactional
    public void execute(LegacyUpdateStockCommand command) {
        idResolver.resolve(command.setofProductGroupId());
        List<UpdateProductStockCommand> commands = command.commands();

        for (UpdateProductStockCommand cmd : commands) {
            updateProductStockUseCase.execute(cmd);
        }
    }
}
