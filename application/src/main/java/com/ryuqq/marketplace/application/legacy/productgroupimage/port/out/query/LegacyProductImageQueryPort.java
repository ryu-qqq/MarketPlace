package com.ryuqq.marketplace.application.legacy.productgroupimage.port.out.query;

import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.util.List;

/** 세토프 DB product_group_image 조회 Port. */
public interface LegacyProductImageQueryPort {

    List<ProductGroupImage> findByProductGroupId(long productGroupId);
}
