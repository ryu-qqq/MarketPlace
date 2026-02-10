package com.ryuqq.marketplace.application.brandmapping.manager;

import com.ryuqq.marketplace.application.brandmapping.port.out.query.BrandMappingQueryPort;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandmapping.exception.BrandMappingNotFoundException;
import com.ryuqq.marketplace.domain.brandmapping.id.BrandMappingId;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** BrandMapping Read Manager. */
@Component
public class BrandMappingReadManager {

    private final BrandMappingQueryPort queryPort;

    public BrandMappingReadManager(BrandMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public BrandMapping getById(BrandMappingId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new BrandMappingNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<BrandMapping> findByCriteria(BrandMappingSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(BrandMappingSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public boolean existsBySalesChannelBrandId(Long salesChannelBrandId) {
        return queryPort.existsBySalesChannelBrandId(salesChannelBrandId);
    }
}
