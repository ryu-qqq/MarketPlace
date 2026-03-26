package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductCommandManager;
import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductReadManager;
import com.ryuqq.marketplace.application.legacy.product.port.in.command.LegacyProductUpdateStockUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/** 레거시 상품 재고 수정 서비스. */
@Service
public class LegacyProductUpdateStockService implements LegacyProductUpdateStockUseCase {

    private final LegacyProductReadManager productReadManager;
    private final LegacyProductCommandManager productCommandManager;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateStockService(
            LegacyProductReadManager productReadManager,
            LegacyProductCommandManager productCommandManager,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.productReadManager = productReadManager;
        this.productCommandManager = productCommandManager;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(long productGroupId, List<UpdateProductStockCommand> commands) {
        Instant now = Instant.now();

        List<Product> products = productReadManager.findByProductGroupId(productGroupId);
        Map<Long, Product> productById =
                products.stream().collect(Collectors.toMap(p -> p.idValue(), Function.identity()));

        for (UpdateProductStockCommand command : commands) {
            Product product = productById.get(command.productId());
            if (product != null) {
                product.updateStock(command.stockQuantity(), now);
                productCommandManager.persist(product);
            }
        }

        conversionOutboxCommandManager.createIfNoPending(productGroupId, now);
    }
}
