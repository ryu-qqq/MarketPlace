package com.ryuqq.marketplace.application.product.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.product.dto.command.ChangeProductStatusCommand;
import com.ryuqq.marketplace.application.product.factory.ProductCommandFactory;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.product.port.in.command.ChangeProductStatusUseCase;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import org.springframework.stereotype.Service;

/**
 * ChangeProductStatusService - 상품(SKU) 상태 변경 Service.
 *
 * <p>targetStatus에 따라 activate/deactivate/markSoldOut 도메인 메서드를 호출합니다.
 */
@Service
public class ChangeProductStatusService implements ChangeProductStatusUseCase {

    private final ProductCommandFactory commandFactory;
    private final ProductReadManager readManager;
    private final ProductCommandManager commandManager;

    public ChangeProductStatusService(
            ProductCommandFactory commandFactory,
            ProductReadManager readManager,
            ProductCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(ChangeProductStatusCommand command) {
        StatusChangeContext<ProductId> context = commandFactory.createStatusChangeContext(command);

        Product product = readManager.getById(context.id());
        ProductStatus targetStatus = ProductStatus.valueOf(command.targetStatus());

        switch (targetStatus) {
            case ACTIVE -> product.activate(context.changedAt());
            case INACTIVE -> product.deactivate(context.changedAt());
            case SOLDOUT -> product.markSoldOut(context.changedAt());
            default -> throw new IllegalArgumentException("지원하지 않는 상태 변경입니다: " + targetStatus);
        }

        commandManager.persist(product);
    }
}
