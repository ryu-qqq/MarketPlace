package com.ryuqq.marketplace.application.channeloptionmapping.manager;

import com.ryuqq.marketplace.application.channeloptionmapping.port.out.query.ChannelOptionMappingQueryPort;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import com.ryuqq.marketplace.domain.channeloptionmapping.exception.ChannelOptionMappingNotFoundException;
import com.ryuqq.marketplace.domain.channeloptionmapping.id.ChannelOptionMappingId;
import com.ryuqq.marketplace.domain.channeloptionmapping.query.ChannelOptionMappingSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ChannelOptionMapping Read Manager. */
@Component
public class ChannelOptionMappingReadManager {

    private final ChannelOptionMappingQueryPort queryPort;

    public ChannelOptionMappingReadManager(ChannelOptionMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public ChannelOptionMapping getById(ChannelOptionMappingId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new ChannelOptionMappingNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<ChannelOptionMapping> findByCriteria(ChannelOptionMappingSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ChannelOptionMappingSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public boolean existsBySalesChannelIdAndCanonicalOptionValueId(
            SalesChannelId salesChannelId, CanonicalOptionValueId canonicalOptionValueId) {
        return queryPort.existsBySalesChannelIdAndCanonicalOptionValueId(
                salesChannelId, canonicalOptionValueId);
    }
}
