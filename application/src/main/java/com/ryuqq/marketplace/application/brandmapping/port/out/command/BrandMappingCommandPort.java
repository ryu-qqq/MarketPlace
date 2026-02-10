package com.ryuqq.marketplace.application.brandmapping.port.out.command;

import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;

/** BrandMapping Command Port. */
public interface BrandMappingCommandPort {
    Long persist(BrandMapping brandMapping);
}
