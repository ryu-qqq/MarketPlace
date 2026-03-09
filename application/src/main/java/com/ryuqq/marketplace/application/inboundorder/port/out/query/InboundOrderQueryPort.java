package com.ryuqq.marketplace.application.inboundorder.port.out.query;

import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** InboundOrder 조회 포트. */
public interface InboundOrderQueryPort {

    boolean existsBySalesChannelIdAndExternalOrderNo(long salesChannelId, String externalOrderNo);

    /** salesChannelId + externalOrderNo 목록으로 이미 존재하는 주문번호를 일괄 조회합니다. */
    Set<String> findExistingExternalOrderNos(long salesChannelId, Set<String> externalOrderNos);

    Optional<Instant> findLastExternalOrderedAt(long salesChannelId);

    List<InboundOrder> findByStatus(InboundOrderStatus status, int limit);
}
