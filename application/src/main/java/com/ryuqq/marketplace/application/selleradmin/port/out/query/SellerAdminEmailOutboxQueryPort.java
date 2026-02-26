package com.ryuqq.marketplace.application.selleradmin.port.out.query;

import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 셀러 관리자 이메일 Outbox 조회 포트.
 *
 * <p>이메일 Outbox 조회를 위한 아웃바운드 포트입니다.
 */
public interface SellerAdminEmailOutboxQueryPort {

    /**
     * 셀러 ID로 PENDING 상태의 이메일 Outbox를 조회합니다.
     *
     * @param sellerId 셀러 ID
     * @return Outbox Optional
     */
    Optional<SellerAdminEmailOutbox> findPendingBySellerId(SellerId sellerId);

    /**
     * 처리 대기 중인 이메일 Outbox 목록을 조회합니다 (스케줄러용).
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<SellerAdminEmailOutbox> findPendingOutboxesForRetry(Instant beforeTime, int limit);

    /**
     * PROCESSING 타임아웃 이메일 Outbox 목록을 조회합니다 (스케줄러용).
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<SellerAdminEmailOutbox> findProcessingTimeoutOutboxes(Instant timeoutThreshold, int limit);
}
