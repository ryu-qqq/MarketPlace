package com.ryuqq.marketplace.application.outboundsync.port.out.query;

import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatusSummary;
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

    /** 여러 상품그룹 ID로 PENDING 상태의 Outbox 일괄 조회. */
    List<OutboundSyncOutbox> findPendingByProductGroupIds(
            java.util.Collection<ProductGroupId> productGroupIds);

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

    /**
     * 상품그룹 ID별 연동 이력 페이징 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @param status 상태 필터 (null이면 전체)
     * @param offset 오프셋
     * @param limit 조회 건수
     * @return Outbox 목록
     */
    List<OutboundSyncOutbox> findPagedByProductGroupId(
            Long productGroupId, SyncStatus status, long offset, int limit);

    /**
     * 상품그룹 ID별 연동 이력 건수 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @param status 상태 필터 (null이면 전체)
     * @return 건수
     */
    long countByProductGroupIdAndStatus(Long productGroupId, SyncStatus status);

    /**
     * 상품그룹 ID별 연동 상태 요약 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @return 연동 상태 요약 (완료/실패/대기 건수 + 마지막 연동일)
     */
    SyncStatusSummary getSyncSummary(Long productGroupId);
}
