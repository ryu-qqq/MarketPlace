package com.ryuqq.marketplace.application.legacy.productgroupimage.manager;

import com.ryuqq.marketplace.application.legacy.productgroupimage.port.out.query.LegacyProductImageQueryPort;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
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
    public ProductGroupImages getByProductGroupId(long productGroupId) {
        return ProductGroupImages.reconstitute(queryPort.findByProductGroupId(productGroupId));
    }
}
