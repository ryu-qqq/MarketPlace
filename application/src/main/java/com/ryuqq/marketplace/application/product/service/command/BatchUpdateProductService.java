package com.ryuqq.marketplace.application.product.service.command;

import com.ryuqq.marketplace.application.outboundsync.internal.ProductGroupUpdateOutboxCoordinator;
import com.ryuqq.marketplace.application.product.dto.command.BatchUpdateProductCommand;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.port.in.command.BatchUpdateProductUseCase;
import com.ryuqq.marketplace.application.product.validator.ProductOwnershipValidator;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * BatchUpdateProductService - 상품(SKU) 배치 가격/재고 수정 Service.
 *
 * <p>셀러 소유권을 검증한 뒤, 상품들의 가격과 재고를 일괄 수정합니다.
 */
@Service
public class BatchUpdateProductService implements BatchUpdateProductUseCase {

    private final ProductOwnershipValidator ownershipValidator;
    private final ProductCommandManager commandManager;
    private final ProductGroupUpdateOutboxCoordinator updateOutboxCoordinator;

    public BatchUpdateProductService(
            ProductOwnershipValidator ownershipValidator,
            ProductCommandManager commandManager,
            ProductGroupUpdateOutboxCoordinator updateOutboxCoordinator) {
        this.ownershipValidator = ownershipValidator;
        this.commandManager = commandManager;
        this.updateOutboxCoordinator = updateOutboxCoordinator;
    }

    @Override
    public void execute(BatchUpdateProductCommand command) {
        List<ProductId> productIds =
                command.entries().stream().map(e -> ProductId.of(e.productId())).toList();

        List<Product> products = ownershipValidator.validateAndGet(productIds, command.sellerId());

        Map<Long, BatchUpdateProductCommand.Entry> entryMap =
                command.entries().stream()
                        .collect(
                                Collectors.toMap(
                                        BatchUpdateProductCommand.Entry::productId,
                                        Function.identity()));

        Instant now = Instant.now();

        for (Product product : products) {
            BatchUpdateProductCommand.Entry entry = entryMap.get(product.idValue());
            product.updatePrice(
                    Money.of(entry.regularPrice()), Money.of(entry.currentPrice()), now);
            product.updateStock(entry.stockQuantity(), now);
        }

        commandManager.persistAll(products);

        Set<Long> processedGroupIds = new HashSet<>();
        for (Product product : products) {
            if (processedGroupIds.add(product.productGroupIdValue())) {
                updateOutboxCoordinator.createUpdateOutboxesIfNeeded(
                        ProductGroupId.of(product.productGroupIdValue()));
            }
        }
    }
}
