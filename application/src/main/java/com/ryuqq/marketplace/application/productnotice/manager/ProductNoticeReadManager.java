package com.ryuqq.marketplace.application.productnotice.manager;

import com.ryuqq.marketplace.application.productnotice.port.out.query.ProductNoticeQueryPort;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductNotice Read Manager. */
@Component
public class ProductNoticeReadManager {

    private final ProductNoticeQueryPort queryPort;

    public ProductNoticeReadManager(ProductNoticeQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Optional<ProductNotice> findByProductGroupId(ProductGroupId productGroupId) {
        return queryPort.findByProductGroupId(productGroupId);
    }
}
