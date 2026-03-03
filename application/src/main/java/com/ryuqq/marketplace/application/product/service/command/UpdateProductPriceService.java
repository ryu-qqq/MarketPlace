package com.ryuqq.marketplace.application.product.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.outboundsync.internal.ProductGroupUpdateOutboxCoordinator;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductPriceCommand;
import com.ryuqq.marketplace.application.product.factory.ProductCommandFactory;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductPriceUseCase;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import org.springframework.stereotype.Service;

/**
 * UpdateProductPriceService - 상품(SKU) 가격 수정 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 */
@Service
public class UpdateProductPriceService implements UpdateProductPriceUseCase {

    private final ProductCommandFactory commandFactory;
    private final ProductReadManager readManager;
    private final ProductCommandManager commandManager;
    private final ProductGroupUpdateOutboxCoordinator updateOutboxCoordinator;

    public UpdateProductPriceService(
            ProductCommandFactory commandFactory,
            ProductReadManager readManager,
            ProductCommandManager commandManager,
            ProductGroupUpdateOutboxCoordinator updateOutboxCoordinator) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.updateOutboxCoordinator = updateOutboxCoordinator;
    }

    @Override
    public void execute(UpdateProductPriceCommand command) {
        StatusChangeContext<ProductId> context = commandFactory.createPriceUpdateContext(command);

        Product product = readManager.getById(context.id());
        product.updatePrice(
                Money.of(command.regularPrice()),
                Money.of(command.currentPrice()),
                context.changedAt());

        commandManager.persist(product);

        updateOutboxCoordinator.createUpdateOutboxesIfNeeded(product.productGroupId());
    }
}
