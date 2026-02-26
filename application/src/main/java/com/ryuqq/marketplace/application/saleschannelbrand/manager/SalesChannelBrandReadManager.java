package com.ryuqq.marketplace.application.saleschannelbrand.manager;

import com.ryuqq.marketplace.application.saleschannelbrand.port.out.query.SalesChannelBrandQueryPort;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import com.ryuqq.marketplace.domain.saleschannelbrand.exception.SalesChannelBrandNotFoundException;
import com.ryuqq.marketplace.domain.saleschannelbrand.id.SalesChannelBrandId;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SalesChannelBrand Read Manager. */
@Component
public class SalesChannelBrandReadManager {

    private final SalesChannelBrandQueryPort queryPort;

    public SalesChannelBrandReadManager(SalesChannelBrandQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public SalesChannelBrand getById(SalesChannelBrandId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new SalesChannelBrandNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<SalesChannelBrand> findByCriteria(SalesChannelBrandSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(SalesChannelBrandSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public boolean existsBySalesChannelIdAndExternalCode(
            Long salesChannelId, String externalBrandCode) {
        return queryPort.existsBySalesChannelIdAndExternalCode(salesChannelId, externalBrandCode);
    }
}
