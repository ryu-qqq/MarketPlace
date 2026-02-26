package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductIdResolver;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductStockUseCase;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 인바운드 상품 재고 수정 서비스. */
@Service
public class UpdateInboundProductStockService implements UpdateInboundProductStockUseCase {

    private final InboundProductIdResolver idResolver;
    private final ProductReadManager productReadManager;
    private final ProductCommandManager productCommandManager;
    private final TimeProvider timeProvider;

    public UpdateInboundProductStockService(
            InboundProductIdResolver idResolver,
            ProductReadManager productReadManager,
            ProductCommandManager productCommandManager,
            TimeProvider timeProvider) {
        this.idResolver = idResolver;
        this.productReadManager = productReadManager;
        this.productCommandManager = productCommandManager;
        this.timeProvider = timeProvider;
    }

    @Override
    @Transactional
    public void execute(
            long inboundSourceId,
            String externalProductCode,
            List<UpdateProductStockCommand> stockCommands) {
        ProductGroupId pgId = idResolver.resolve(inboundSourceId, externalProductCode);
        List<Product> products = productReadManager.findByProductGroupId(pgId);
        Instant now = timeProvider.now();

        Map<Long, Product> productMap =
                products.stream().collect(Collectors.toMap(Product::idValue, Function.identity()));

        for (UpdateProductStockCommand cmd : stockCommands) {
            Product product = productMap.get(cmd.productId());
            if (product != null) {
                product.updateStock(cmd.stockQuantity(), now);
            }
        }
        productCommandManager.persistAll(products);
    }
}
