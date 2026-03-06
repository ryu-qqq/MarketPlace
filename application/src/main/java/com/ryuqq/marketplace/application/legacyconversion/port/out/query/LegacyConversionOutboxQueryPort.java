package com.ryuqq.marketplace.application.legacyconversion.port.out.query;

import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/** 레거시 변환 Outbox 조회 포트. */
public interface LegacyConversionOutboxQueryPort {

    /**
     * 처리 대기 중인 Outbox 목록 조회.
     *
     * @param beforeTime 이 시간 이전에 생성된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<LegacyConversionOutbox> findPendingOutboxes(Instant beforeTime, int limit);

    /**
     * PROCESSING 타임아웃 Outbox 목록 조회.
     *
     * @param timeoutThreshold 이 시간 이전에 업데이트된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<LegacyConversionOutbox> findProcessingTimeoutOutboxes(Instant timeoutThreshold, int limit);

    /**
     * 해당 legacyProductGroupId에 PENDING 상태 Outbox가 존재하는지 확인.
     *
     * @param legacyProductGroupId 레거시 상품그룹 ID
     * @return PENDING 존재 여부
     */
    boolean existsPendingByLegacyProductGroupId(long legacyProductGroupId);

    /**
     * 주어진 레거시 상품그룹 ID 중 이미 Outbox에 존재하는 ID 집합 조회 (모든 상태 포함).
     *
     * @param legacyProductGroupIds 확인할 ID 목록
     * @return 이미 Outbox에 존재하는 ID 집합
     */
    Set<Long> findExistingLegacyProductGroupIds(Collection<Long> legacyProductGroupIds);

    /**
     * Outbox에 등록된 고유 레거시 상품그룹 ID 수를 반환합니다.
     *
     * @return 등록된 고유 레거시 상품그룹 ID 수
     */
    long countDistinctLegacyProductGroupIds();
}
