package com.ryuqq.marketplace.application.brand.port.out.command;

import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;

public interface BrandPersistencePort {
    Brand persist(Brand brand);
    boolean existsByCode(String code);
    boolean existsByCanonicalName(String canonicalName);
}
