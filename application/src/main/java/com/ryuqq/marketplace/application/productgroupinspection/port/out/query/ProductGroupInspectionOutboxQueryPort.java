package com.ryuqq.marketplace.application.productgroupinspection.port.out.query;

import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** 검수 Outbox Query Port. */
public interface ProductGroupInspectionOutboxQueryPort {

    /**
     * 처리 대기 중인 Outbox 목록 조회 (Outbox Relay 스케줄러용).
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<ProductGroupInspectionOutbox> findPendingOutboxes(Instant beforeTime, int limit);

    /**
     * 진행 중 상태 타임아웃 Outbox 목록 조회 (타임아웃 복구 스케줄러용).
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<ProductGroupInspectionOutbox> findInProgressTimeoutOutboxes(
            Instant timeoutThreshold, int limit);

    /**
     * ID로 Outbox 단건 조회.
     *
     * @param outboxId Outbox ID
     * @return Outbox
     */
    Optional<ProductGroupInspectionOutbox> findById(Long outboxId);
}
