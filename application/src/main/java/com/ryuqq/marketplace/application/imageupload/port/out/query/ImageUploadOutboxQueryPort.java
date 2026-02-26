package com.ryuqq.marketplace.application.imageupload.port.out.query;

import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import java.time.Instant;
import java.util.List;

/**
 * ImageUploadOutbox Query Port.
 *
 * <p>이미지 업로드 Outbox 조회를 위한 포트입니다.
 */
public interface ImageUploadOutboxQueryPort {

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
    List<ImageUploadOutbox> findPendingOutboxesForRetry(Instant beforeTime, int limit);

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
    List<ImageUploadOutbox> findProcessingTimeoutOutboxes(Instant timeoutThreshold, int limit);

    /**
     * sourceId 목록과 sourceType으로 Outbox 목록 조회.
     *
     * <p>상품 그룹 이미지/상세설명 이미지의 업로드 상태 조회에 사용됩니다.
     *
     * @param sourceIds 이미지 ID 목록
     * @param sourceType 이미지 소스 타입
     * @return Outbox 목록 (sourceId, createdAt DESC 정렬)
     */
    List<ImageUploadOutbox> findBySourceIdsAndSourceType(
            List<Long> sourceIds, ImageSourceType sourceType);
}
