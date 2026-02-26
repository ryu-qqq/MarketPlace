package com.ryuqq.marketplace.application.brandpreset.manager;

import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import com.ryuqq.marketplace.application.brandpreset.port.out.query.BrandPresetQueryPort;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetNotFoundException;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** BrandPreset Read Manager. */
@Component
public class BrandPresetReadManager {

    private final BrandPresetQueryPort queryPort;

    public BrandPresetReadManager(BrandPresetQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public BrandPreset getById(BrandPresetId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new BrandPresetNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<BrandPresetResult> findByCriteria(BrandPresetSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(BrandPresetSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public List<BrandPreset> findAllByIds(List<Long> ids) {
        return queryPort.findAllByIds(ids);
    }

    @Transactional(readOnly = true)
    public Optional<Long> findSalesChannelIdBySalesChannelBrandId(Long salesChannelBrandId) {
        return queryPort.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId);
    }
}
