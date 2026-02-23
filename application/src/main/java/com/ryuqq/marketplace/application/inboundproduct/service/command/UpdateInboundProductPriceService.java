package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductIdResolver;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductPriceUseCase;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 인바운드 상품 가격 수정 서비스. */
@Service
public class UpdateInboundProductPriceService implements UpdateInboundProductPriceUseCase {

    private final InboundProductIdResolver idResolver;
    private final ProductReadManager productReadManager;
    private final ProductCommandManager productCommandManager;
    private final TimeProvider timeProvider;

    public UpdateInboundProductPriceService(
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
            long inboundSourceId, String externalProductCode, int regularPrice, int currentPrice) {
        ProductGroupId pgId = idResolver.resolve(inboundSourceId, externalProductCode);
        List<Product> products = productReadManager.findByProductGroupId(pgId);
        Instant now = timeProvider.now();

        for (Product product : products) {
            product.updatePrice(Money.of(regularPrice), Money.of(currentPrice), now);
        }
        productCommandManager.persistAll(products);
    }
}
