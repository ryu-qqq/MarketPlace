package com.ryuqq.marketplace.application.legacy.product.manager;

import com.ryuqq.marketplace.application.legacy.product.port.out.query.LegacyProductQueryPort;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProducts;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 상품 Read Manager. */
@Component
public class LegacyProductReadManager {

    private final LegacyProductQueryPort queryPort;

    public LegacyProductReadManager(LegacyProductQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public LegacyProducts getByProductGroupId(LegacyProductGroupId productGroupId) {
        return new LegacyProducts(queryPort.findByProductGroupId(productGroupId));
    }
}
