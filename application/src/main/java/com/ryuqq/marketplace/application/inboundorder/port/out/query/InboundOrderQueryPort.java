package com.ryuqq.marketplace.application.inboundorder.port.out.query;

import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** InboundOrder 조회 포트. */
public interface InboundOrderQueryPort {

    boolean existsBySalesChannelIdAndExternalOrderNo(long salesChannelId, String externalOrderNo);

    Optional<Instant> findLastExternalOrderedAt(long salesChannelId);

    List<InboundOrder> findByStatus(InboundOrderStatus status, int limit);
}
