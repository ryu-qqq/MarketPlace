package com.ryuqq.marketplace.application.productgroupimage.port.out.command;

import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;

/** ProductGroupImage Command Port. */
public interface ProductGroupImageCommandPort {

    Long persist(ProductGroupImage image);
}
