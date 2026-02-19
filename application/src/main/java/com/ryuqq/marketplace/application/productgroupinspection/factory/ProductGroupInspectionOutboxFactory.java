package com.ryuqq.marketplace.application.productgroupinspection.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import org.springframework.stereotype.Component;

/** 검수 Outbox Factory. */
@Component
public class ProductGroupInspectionOutboxFactory {

    private final TimeProvider timeProvider;

    public ProductGroupInspectionOutboxFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public ProductGroupInspectionOutbox create(Long productGroupId) {
        return ProductGroupInspectionOutbox.forNew(productGroupId, timeProvider.now());
    }
}
