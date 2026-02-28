package com.ryuqq.marketplace.application.product.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.product.dto.command.BatchChangeProductStatusCommand;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductOptionMappingCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.product.port.in.command.BatchChangeProductStatusUseCase;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * BatchChangeProductStatusService - 상품(SKU) 배치 상태 변경 Service.
 *
 * <p>ProductGroup 소유권(sellerId)을 먼저 검증한 뒤, 해당 그룹 내 상품들의 상태를 일괄 변경합니다.
 */
@Service
public class BatchChangeProductStatusService implements BatchChangeProductStatusUseCase {

    private final TimeProvider timeProvider;
    private final ProductGroupReadManager productGroupReadManager;
    private final ProductReadManager readManager;
    private final ProductCommandManager commandManager;
    private final ProductOptionMappingCommandManager optionMappingCommandManager;

    public BatchChangeProductStatusService(
            TimeProvider timeProvider,
            ProductGroupReadManager productGroupReadManager,
            ProductReadManager readManager,
            ProductCommandManager commandManager,
            ProductOptionMappingCommandManager optionMappingCommandManager) {
        this.timeProvider = timeProvider;
        this.productGroupReadManager = productGroupReadManager;
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.optionMappingCommandManager = optionMappingCommandManager;
    }

    @Override
    public void execute(BatchChangeProductStatusCommand command) {
        ProductGroupId productGroupId = ProductGroupId.of(command.productGroupId());

        productGroupReadManager.getByIdsAndSellerId(List.of(productGroupId), command.sellerId());

        List<ProductId> productIds = command.productIds().stream().map(ProductId::of).toList();

        List<Product> products = readManager.getByProductGroupIdAndIds(productGroupId, productIds);

        ProductStatus targetStatus = ProductStatus.valueOf(command.targetStatus());
        Instant changedAt = timeProvider.now();

        for (Product product : products) {
            product.changeStatus(targetStatus, changedAt);
        }

        commandManager.persistAll(products);

        if (targetStatus == ProductStatus.DELETED) {
            for (Product product : products) {
                optionMappingCommandManager.persistAll(product.optionMappings());
            }
        }
    }
}
