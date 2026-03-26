package com.ryuqq.marketplace.application.legacy.product.port.out.query;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import java.util.List;

/** 세토프 DB option_group + option_detail 조회 Port. */
public interface LegacySellerOptionQueryPort {

    List<SellerOptionGroup> findByProductGroupId(long productGroupId);
}
