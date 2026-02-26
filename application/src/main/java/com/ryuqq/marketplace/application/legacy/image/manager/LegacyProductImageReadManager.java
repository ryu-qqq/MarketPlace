package com.ryuqq.marketplace.application.legacy.image.manager;

import com.ryuqq.marketplace.application.legacy.image.port.out.query.LegacyProductImageQueryPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImages;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 상품이미지 Read Manager. */
@Component
public class LegacyProductImageReadManager {

    private final LegacyProductImageQueryPort queryPort;

    public LegacyProductImageReadManager(LegacyProductImageQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public LegacyProductImages getByProductGroupId(LegacyProductGroupId productGroupId) {
        return new LegacyProductImages(queryPort.findByProductGroupId(productGroupId));
    }
}
