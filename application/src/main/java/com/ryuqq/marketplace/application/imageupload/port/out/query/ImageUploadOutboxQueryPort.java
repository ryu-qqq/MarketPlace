package com.ryuqq.marketplace.application.imageupload.port.out.query;

import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    /**
     * PROCESSING 상태의 Outbox 목록 조회 (폴링 스케줄러용).
     *
     * <p>조건:
     *
     * <ul>
     *   <li>status = PROCESSING
     *   <li>download_task_id IS NOT NULL
     * </ul>
     *
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<ImageUploadOutbox> findProcessingOutboxes(int limit);

    /**
     * 복구 가능한 FAILED Outbox 목록 조회.
     *
     * <p>조건:
     *
     * <ul>
     *   <li>status = FAILED
     *   <li>processed_at < failedBefore (일정 시간 경과)
     *   <li>error_message NOT LIKE '%잘못된 요청%' (복구 불가 제외)
     * </ul>
     *
     * @param failedBefore 이 시간 이전에 FAILED된 것만 조회
     * @param limit 최대 조회 개수
     * @return Outbox 목록
     */
    List<ImageUploadOutbox> findRecoverableFailedOutboxes(Instant failedBefore, int limit);

    /**
     * downloadTaskId로 PROCESSING 상태의 Outbox를 조회합니다 (콜백용).
     *
     * @param downloadTaskId FileFlow 다운로드 태스크 ID
     * @return Outbox (Optional)
     */
    Optional<ImageUploadOutbox> findProcessingByDownloadTaskId(String downloadTaskId);
}
