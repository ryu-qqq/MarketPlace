package com.ryuqq.marketplace.application.productnotice.port.out.query;

import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.util.List;
import java.util.Optional;

/** ProductNotice Query Port. */
public interface ProductNoticeQueryPort {

    Optional<ProductNotice> findByProductGroupId(ProductGroupId productGroupId);

    List<ProductNotice> findByProductGroupIdIn(List<ProductGroupId> productGroupIds);
}
