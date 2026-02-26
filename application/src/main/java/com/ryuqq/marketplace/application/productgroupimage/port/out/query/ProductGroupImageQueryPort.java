package com.ryuqq.marketplace.application.productgroupimage.port.out.query;

import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.util.List;
import java.util.Optional;

/** ProductGroupImage Query Port. */
public interface ProductGroupImageQueryPort {

    Optional<ProductGroupImage> findById(Long id);

    List<ProductGroupImage> findByProductGroupId(ProductGroupId productGroupId);

    List<ProductGroupImage> findByProductGroupIdIn(List<ProductGroupId> productGroupIds);
}
