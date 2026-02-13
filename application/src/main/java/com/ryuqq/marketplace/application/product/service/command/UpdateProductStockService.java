package com.ryuqq.marketplace.application.product.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.factory.ProductCommandFactory;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductStockUseCase;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import org.springframework.stereotype.Service;

/**
 * UpdateProductStockService - 상품(SKU) 재고 수정 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Service
public class UpdateProductStockService implements UpdateProductStockUseCase {

    private final ProductCommandFactory commandFactory;
    private final ProductReadManager readManager;
    private final ProductCommandManager commandManager;

    public UpdateProductStockService(
            ProductCommandFactory commandFactory,
            ProductReadManager readManager,
            ProductCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateProductStockCommand command) {
        StatusChangeContext<ProductId> context = commandFactory.createStockUpdateContext(command);

        Product product = readManager.getById(context.id());
        product.updateStock(command.stockQuantity(), context.changedAt());

        commandManager.persist(product);
    }
}
