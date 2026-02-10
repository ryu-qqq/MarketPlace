package com.ryuqq.marketplace.application.saleschannelbrand.port.out.command;

import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;

/** SalesChannelBrand Command Port. */
public interface SalesChannelBrandCommandPort {
    Long persist(SalesChannelBrand brand);
}
