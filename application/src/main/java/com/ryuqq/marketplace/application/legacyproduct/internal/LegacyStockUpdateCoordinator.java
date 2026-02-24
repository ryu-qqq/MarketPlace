package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductStockUseCase;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 재고 수정 Coordinator.
 *
 * <p>레거시 재고 수정은 내부 productId를 직접 사용하므로 setof PK 변환이 불필요합니다.
 */
@Component
public class LegacyStockUpdateCoordinator {

    private final UpdateProductStockUseCase updateProductStockUseCase;

    public LegacyStockUpdateCoordinator(UpdateProductStockUseCase updateProductStockUseCase) {
        this.updateProductStockUseCase = updateProductStockUseCase;
    }

    @Transactional
    public void execute(List<UpdateProductStockCommand> commands) {
        for (UpdateProductStockCommand cmd : commands) {
            updateProductStockUseCase.execute(cmd);
        }
    }
}
