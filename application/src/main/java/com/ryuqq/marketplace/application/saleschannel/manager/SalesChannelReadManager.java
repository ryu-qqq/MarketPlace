package com.ryuqq.marketplace.application.saleschannel.manager;

import com.ryuqq.marketplace.application.saleschannel.port.out.query.SalesChannelQueryPort;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.exception.SalesChannelNotFoundException;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SalesChannel Read Manager. */
@Component
public class SalesChannelReadManager {

    private final SalesChannelQueryPort queryPort;

    public SalesChannelReadManager(SalesChannelQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public SalesChannel getById(SalesChannelId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new SalesChannelNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<SalesChannel> findByCriteria(SalesChannelSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(SalesChannelSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public boolean existsByChannelName(String channelName) {
        return queryPort.existsByChannelName(channelName);
    }
}
