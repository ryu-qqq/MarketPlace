package com.ryuqq.marketplace.application.saleschannel.port.out.query;

import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
import java.util.List;
import java.util.Optional;

/** SalesChannel Query Port. */
public interface SalesChannelQueryPort {
    Optional<SalesChannel> findById(SalesChannelId id);

    List<SalesChannel> findByCriteria(SalesChannelSearchCriteria criteria);

    long countByCriteria(SalesChannelSearchCriteria criteria);

    boolean existsByChannelName(String channelName);

    boolean existsByChannelNameExcluding(String channelName, SalesChannelId excludeId);
}
