package com.ryuqq.marketplace.application.externalproductsync.port.out.query;

import com.ryuqq.marketplace.domain.externalproductsync.aggregate.ExternalProductSyncOutbox;
import java.util.List;

/** 외부 상품 연동 Outbox 조회 포트. */
public interface ExternalProductSyncOutboxQueryPort {

    /**
     * 상품그룹 ID로 PENDING 상태의 Outbox 목록 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @return PENDING 상태의 Outbox 목록
     */
    List<ExternalProductSyncOutbox> findPendingByProductGroupId(Long productGroupId);
}
