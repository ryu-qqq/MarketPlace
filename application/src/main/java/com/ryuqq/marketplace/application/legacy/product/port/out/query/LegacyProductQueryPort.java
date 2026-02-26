package com.ryuqq.marketplace.application.legacy.product.port.out.query;

import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.util.List;

/** 세토프 DB product 테이블 조회 Port. */
public interface LegacyProductQueryPort {

    List<LegacyProduct> findByProductGroupId(LegacyProductGroupId productGroupId);
}
