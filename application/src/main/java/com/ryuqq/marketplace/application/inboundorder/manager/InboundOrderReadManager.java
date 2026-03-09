package com.ryuqq.marketplace.application.inboundorder.manager;

import com.ryuqq.marketplace.application.inboundorder.port.out.query.InboundOrderQueryPort;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** InboundOrder 조회 Manager. */
@Component
public class InboundOrderReadManager {

    private final InboundOrderQueryPort queryPort;

    public InboundOrderReadManager(InboundOrderQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public boolean existsBySalesChannelIdAndExternalOrderNo(
            long salesChannelId, String externalOrderNo) {
        return queryPort.existsBySalesChannelIdAndExternalOrderNo(salesChannelId, externalOrderNo);
    }

    @Transactional(readOnly = true)
    public Set<String> findExistingExternalOrderNos(
            long salesChannelId, Set<String> externalOrderNos) {
        return queryPort.findExistingExternalOrderNos(salesChannelId, externalOrderNos);
    }

    @Transactional(readOnly = true)
    public Optional<Instant> findLastExternalOrderedAt(long salesChannelId) {
        return queryPort.findLastExternalOrderedAt(salesChannelId);
    }

    @Transactional(readOnly = true)
    public List<InboundOrder> findByStatus(InboundOrderStatus status, int limit) {
        return queryPort.findByStatus(status, limit);
    }
}
