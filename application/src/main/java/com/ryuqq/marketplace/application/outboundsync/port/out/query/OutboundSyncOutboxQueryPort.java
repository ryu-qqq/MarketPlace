package com.ryuqq.marketplace.application.outboundsync.port.out.query;

import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;

/** 외부 상품 연동 Outbox 조회 포트. */
public interface OutboundSyncOutboxQueryPort {

    /**
     * 상품그룹 ID로 PENDING 상태의 Outbox 목록 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @return PENDING 상태의 Outbox 목록
     */
    List<OutboundSyncOutbox> findPendingByProductGroupId(ProductGroupId productGroupId);
}
