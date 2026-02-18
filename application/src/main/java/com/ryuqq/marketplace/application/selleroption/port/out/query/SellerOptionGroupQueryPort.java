package com.ryuqq.marketplace.application.selleroption.port.out.query;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;

/** SellerOptionGroup Query Port. */
public interface SellerOptionGroupQueryPort {

    List<SellerOptionGroup> findByProductGroupId(ProductGroupId productGroupId);
}
