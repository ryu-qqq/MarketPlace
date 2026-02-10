package com.ryuqq.marketplace.application.saleschannelcategory.port.out.command;

import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;

/** SalesChannelCategory Command Port. */
public interface SalesChannelCategoryCommandPort {
    Long persist(SalesChannelCategory salesChannelCategory);
}
