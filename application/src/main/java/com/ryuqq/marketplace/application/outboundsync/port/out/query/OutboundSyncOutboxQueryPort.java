package com.ryuqq.marketplace.application.outboundsync.port.out.query;

import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
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

    /**
     * PENDING 상태이고 beforeTime 이전에 생성된 Outbox 목록 조회.
     *
     * @param beforeTime 생성일시 기준
     * @param batchSize 최대 조회 건수
     * @return PENDING 상태의 Outbox 목록
     */
    List<OutboundSyncOutbox> findPendingOutboxes(Instant beforeTime, int batchSize);

    /**
     * PROCESSING 상태에서 타임아웃된 Outbox 목록 조회.
     *
     * @param timeoutBefore updatedAt 기준 타임아웃 시각
     * @param batchSize 최대 조회 건수
     * @return 타임아웃된 PROCESSING 상태의 Outbox 목록
     */
    List<OutboundSyncOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int batchSize);

    /**
     * ID로 Outbox 조회.
     *
     * @param outboxId Outbox ID
     * @return Outbox
     * @throws IllegalStateException 존재하지 않는 경우
     */
    OutboundSyncOutbox getById(Long outboxId);

    /**
     * 상품그룹 ID + syncType으로 PENDING/PROCESSING 상태의 Outbox 목록 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @param syncType 연동 타입
     * @return 활성 상태의 Outbox 목록
     */
    List<OutboundSyncOutbox> findActiveByProductGroupIdAndSyncType(
            ProductGroupId productGroupId, SyncType syncType);
}
