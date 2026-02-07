package com.ryuqq.marketplace.application.brand.manager;

import com.ryuqq.marketplace.application.brand.port.out.query.BrandQueryPort;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brand.exception.BrandNotFoundException;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Brand Read Manager. */
@Component
public class BrandReadManager {

    private final BrandQueryPort queryPort;

    public BrandReadManager(BrandQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Brand getById(BrandId id) {
        return queryPort.findById(id).orElseThrow(() -> new BrandNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<Brand> findByCriteria(BrandSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(BrandSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return queryPort.existsByCode(code);
    }
}
