package com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.query;

import com.ryuqq.marketplace.domain.legacy.productdescription.aggregate.LegacyProductGroupDescription;
import java.util.Optional;

/**
 * 레거시 상품그룹 상세설명 Read Port.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface LegacyProductGroupDescriptionReadPort {

    Optional<LegacyProductGroupDescription> findByProductGroupId(long productGroupId);
}
