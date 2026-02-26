package com.ryuqq.marketplace.application.legacy.image.port.out.query;

import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import java.util.List;

/** 세토프 DB product_group_image 조회 Port. */
public interface LegacyProductImageQueryPort {

    List<LegacyProductImage> findByProductGroupId(LegacyProductGroupId productGroupId);
}
