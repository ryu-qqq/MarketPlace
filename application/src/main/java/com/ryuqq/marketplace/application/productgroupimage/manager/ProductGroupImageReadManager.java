package com.ryuqq.marketplace.application.productgroupimage.manager;

import com.ryuqq.marketplace.application.productgroupimage.port.out.query.ProductGroupImageQueryPort;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.exception.ProductGroupImageNotFoundException;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductGroupImage Read Manager. */
@Component
public class ProductGroupImageReadManager {

    private final ProductGroupImageQueryPort queryPort;

    public ProductGroupImageReadManager(ProductGroupImageQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public ProductGroupImage getById(Long id) {
        return queryPort.findById(id).orElseThrow(() -> new ProductGroupImageNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<ProductGroupImage> findByProductGroupIds(List<ProductGroupId> productGroupIds) {
        if (productGroupIds.isEmpty()) {
            return List.of();
        }
        return queryPort.findByProductGroupIdIn(productGroupIds);
    }

    @Transactional(readOnly = true)
    public ProductGroupImages getByProductGroupId(ProductGroupId productGroupId) {
        List<ProductGroupImage> images = queryPort.findByProductGroupId(productGroupId);
        return ProductGroupImages.reconstitute(images);
    }
}
