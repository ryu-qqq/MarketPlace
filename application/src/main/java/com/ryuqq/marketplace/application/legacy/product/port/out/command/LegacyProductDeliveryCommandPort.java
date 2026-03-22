package com.ryuqq.marketplace.application.legacy.product.port.out.command;

import com.ryuqq.marketplace.domain.legacy.productdelivery.aggregate.LegacyProductDelivery;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;

/** 세토프 DB product_delivery 테이블 커맨드 Port. */
public interface LegacyProductDeliveryCommandPort {

    void persist(LegacyProductGroupId productGroupId, LegacyProductDelivery delivery);
}
