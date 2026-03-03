package com.ryuqq.marketplace.application.seller.port.out.query;

import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * SellerAuthOutbox Query Port.
 *
 * <p>Outbox 조회를 위한 포트입니다.
 */
public interface SellerAuthOutboxQueryPort {

    /**
     * ID로 Outbox 조회.
     *
     * @param outboxId Outbox ID
     * @return Outbox
     * @throws IllegalStateException 존재하지 않는 경우
     */
    SellerAuthOutbox getById(Long outboxId);

    /**
     * SellerId로 PENDING 상태의 Outbox 조회.
     *
     * @param sellerId 셀러 ID
     * @return Outbox (없으면 empty)
     */
    Optional<SellerAuthOutbox> findPendingBySellerId(SellerId sellerId);

    /**
     * 처리 대기 중인 Outbox 목록 조회 (스케줄러용).
     *
     * <p>조건:
     *
     * <ul>
     *   <li>status = PENDING
     *   <li>retry_count < max_retry
     *   <li>created_at < beforeTime (즉시 처리 대상 제외)
     * </ul>
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<SellerAuthOutbox> findPendingOutboxesForRetry(Instant beforeTime, int limit);

    /**
     * PROCESSING 타임아웃 Outbox 목록 조회 (스케줄러용).
     *
     * <p>조건:
     *
     * <ul>
     *   <li>status = PROCESSING
     *   <li>updated_at < timeoutThreshold (좀비 상태)
     * </ul>
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<SellerAuthOutbox> findProcessingTimeoutOutboxes(Instant timeoutThreshold, int limit);
}
