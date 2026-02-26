package com.ryuqq.marketplace.application.productgroupimage.manager;

import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import com.ryuqq.marketplace.application.productgroupimage.port.out.query.ProductGroupImageCompositeQueryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductGroupImage Composite Read Manager. */
@Component
public class ProductGroupImageCompositeReadManager {

    private final ProductGroupImageCompositeQueryPort compositeQueryPort;

    public ProductGroupImageCompositeReadManager(
            ProductGroupImageCompositeQueryPort compositeQueryPort) {
        this.compositeQueryPort = compositeQueryPort;
    }

    @Transactional(readOnly = true)
    public ProductGroupImageUploadStatusResult getImageUploadStatus(Long productGroupId) {
        return compositeQueryPort.findImageUploadStatus(productGroupId);
    }
}
