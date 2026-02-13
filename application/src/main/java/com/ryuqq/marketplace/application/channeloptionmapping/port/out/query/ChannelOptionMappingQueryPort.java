package com.ryuqq.marketplace.application.channeloptionmapping.port.out.query;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import com.ryuqq.marketplace.domain.channeloptionmapping.id.ChannelOptionMappingId;
import com.ryuqq.marketplace.domain.channeloptionmapping.query.ChannelOptionMappingSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import java.util.List;
import java.util.Optional;

/** ChannelOptionMapping Query Port. */
public interface ChannelOptionMappingQueryPort {

    Optional<ChannelOptionMapping> findById(ChannelOptionMappingId id);

    List<ChannelOptionMapping> findByCriteria(ChannelOptionMappingSearchCriteria criteria);

    long countByCriteria(ChannelOptionMappingSearchCriteria criteria);

    boolean existsBySalesChannelIdAndCanonicalOptionValueId(
            SalesChannelId salesChannelId, CanonicalOptionValueId canonicalOptionValueId);
}
