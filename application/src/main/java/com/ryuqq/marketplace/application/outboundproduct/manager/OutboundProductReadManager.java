package com.ryuqq.marketplace.application.outboundproduct.manager;

import com.ryuqq.marketplace.application.outboundproduct.port.out.query.OutboundProductQueryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboundProductReadManager {

    private final OutboundProductQueryPort queryPort;

    public OutboundProductReadManager(OutboundProductQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public boolean existsByProductGroupIdAndSalesChannelId(
            Long productGroupId, Long salesChannelId) {
        return queryPort.existsByProductGroupIdAndSalesChannelId(productGroupId, salesChannelId);
    }
}
