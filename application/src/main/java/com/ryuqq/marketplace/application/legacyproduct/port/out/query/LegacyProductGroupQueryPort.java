package com.ryuqq.marketplace.application.legacyproduct.port.out.query;

import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.util.Optional;

/** 세토프 DB product_group 단건 조회 Port. */
public interface LegacyProductGroupQueryPort {

    Optional<LegacyProductGroup> findById(LegacyProductGroupId productGroupId);
}
