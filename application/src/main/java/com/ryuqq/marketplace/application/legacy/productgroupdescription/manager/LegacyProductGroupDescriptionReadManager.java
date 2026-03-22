package com.ryuqq.marketplace.application.legacy.productgroupdescription.manager;

import com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.query.LegacyProductGroupDescriptionReadPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupDescriptionNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 상품그룹 상세설명 Read Manager. */
@Component
public class LegacyProductGroupDescriptionReadManager {

    private final LegacyProductGroupDescriptionReadPort readPort;

    public LegacyProductGroupDescriptionReadManager(
            LegacyProductGroupDescriptionReadPort readPort) {
        this.readPort = readPort;
    }

    @Transactional(readOnly = true)
    public ProductGroupDescription getByProductGroupId(long productGroupId) {
        return readPort.findByProductGroupId(productGroupId)
                .orElseThrow(
                        () -> new ProductGroupDescriptionNotFoundException(
                                ProductGroupId.of(productGroupId)));
    }
}
