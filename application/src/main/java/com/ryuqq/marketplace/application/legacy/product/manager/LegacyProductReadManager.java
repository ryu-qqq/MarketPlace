package com.ryuqq.marketplace.application.legacy.product.manager;

import com.ryuqq.marketplace.application.legacy.product.port.out.query.LegacyProductQueryPort;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import java.util.List;
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
    public List<Product> findByProductGroupId(long productGroupId) {
        return queryPort.findByProductGroupId(productGroupId);
    }
}
