package com.ryuqq.marketplace.application.productgroupdescription.port.out.query;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import java.util.List;
import java.util.Optional;

/** ProductGroupDescription Query Port. */
public interface ProductGroupDescriptionQueryPort {

    Optional<ProductGroupDescription> findById(Long id);

    Optional<ProductGroupDescription> findByProductGroupId(ProductGroupId productGroupId);

    List<ProductGroupDescription> findByPublishStatus(DescriptionPublishStatus status, int limit);

    List<ProductGroupDescription> findByProductGroupIdIn(List<ProductGroupId> productGroupIds);
}
