package com.ryuqq.marketplace.application.productgroup.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupImage;
import java.util.List;

/** ProductGroupImage Command Port. */
public interface ProductGroupImageCommandPort {

    void deleteByProductGroupId(Long productGroupId);

    List<Long> persistAll(Long productGroupId, List<ProductGroupImage> images);
}
