package com.ryuqq.marketplace.application.saleschannelcategory.manager;

import com.ryuqq.marketplace.application.saleschannelcategory.port.out.query.SalesChannelCategoryQueryPort;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.exception.SalesChannelCategoryNotFoundException;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SalesChannelCategory Read Manager. */
@Component
public class SalesChannelCategoryReadManager {

    private final SalesChannelCategoryQueryPort queryPort;

    public SalesChannelCategoryReadManager(SalesChannelCategoryQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public SalesChannelCategory getById(SalesChannelCategoryId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new SalesChannelCategoryNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<SalesChannelCategory> findByCriteria(SalesChannelCategorySearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(SalesChannelCategorySearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public boolean existsBySalesChannelIdAndExternalCode(
            Long salesChannelId, String externalCategoryCode) {
        return queryPort.existsBySalesChannelIdAndExternalCode(
                salesChannelId, externalCategoryCode);
    }
}
