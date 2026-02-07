package com.ryuqq.marketplace.application.brand.port.out.query;

import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria;
import java.util.List;
import java.util.Optional;

/** Brand Query Port. */
public interface BrandQueryPort {
    Optional<Brand> findById(BrandId id);

    List<Brand> findByCriteria(BrandSearchCriteria criteria);

    long countByCriteria(BrandSearchCriteria criteria);

    boolean existsByCode(String code);
}
