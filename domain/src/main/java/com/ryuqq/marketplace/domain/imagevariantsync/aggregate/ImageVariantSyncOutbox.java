package com.ryuqq.marketplace.domain.imagevariantsync.aggregate;

import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariantsync.id.ImageVariantSyncOutboxId;
import com.ryuqq.marketplace.domain.imagevariantsync.vo.ImageVariantSyncOutboxStatus;
import java.time.Instant;

/**
 * 이미지 Variant Sync Outbox Aggregate.
 *
 * <p>이미지 변환 완료 후 세토프 Sync API로 Variant 정보를 동기화하기 위한 Outbox 패턴 구현체입니다.
 *
 * <p>단일 스케줄러 구조로 동작합니다:
 *
 * <ul>
 *   <li>Scheduler: PENDING 픽업 → sourceImageId의 완료된 Variant 조회 → 세토프 Sync API 호출 → COMPLETED/FAILED
 * </ul>
 *
 * <p><strong>동시성 제어</strong>:
 *
 * <ul>
 *   <li>version: 낙관적 락을 위한 버전 필드
 *   <li>sourceImageId 기준 PENDING 중복 체크로 멱등성 보장
 * </ul>
 */
public class ImageVariantSyncOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final ImageVariantSyncOutboxId id;
    private final Long sourceImageId;
    private final ImageSourceType sourceType;
    private ImageVariantSyncOutboxStatus status;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;

    private ImageVariantSyncOutbox(
            ImageVariantSyncOutboxId id,
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageVariantSyncOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version) {
        this.id = id;
        this.sourceImageId = sourceImageId;
        this.sourceType = sourceType;
        this.status = status;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
    }

    /**
     * 새 Outbox 생성.
     *
     * @param sourceImageId 소스 이미지 DB ID
     * @param sourceType 이미지 소스 타입
     * @param now 현재 시각
     * @return 새 ImageVariantSyncOutbox 인스턴스
     */
    public static ImageVariantSyncOutbox forNew(
            Long sourceImageId, ImageSourceType sourceType, Instant now) {
        return new ImageVariantSyncOutbox(
                ImageVariantSyncOutboxId.forNew(),
                sourceImageId,
                sourceType,
                ImageVariantSyncOutboxStatus.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L);
    }

    /**
     * DB에서 재구성.
     *
     * @param id Outbox ID
     * @param sourceImageId 소스 이미지 DB ID
     * @param sourceType 이미지 소스 타입
     * @param status 상태
     * @param retryCount 재시도 횟수
     * @param maxRetry 최대 재시도 횟수
     * @param createdAt 생성일시
     * @param updatedAt 갱신일시
     * @param processedAt 처리일시
     * @param errorMessage 에러 메시지
     * @param version 낙관적 락 버전
     * @return 재구성된 ImageVariantSyncOutbox 인스턴스
     */
    public static ImageVariantSyncOutbox reconstitute(
            ImageVariantSyncOutboxId id,
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageVariantSyncOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version) {
        return new ImageVariantSyncOutbox(
                id,
                sourceImageId,
                sourceType,
                status,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version);
    }

    public boolean isNew() {
        return id.isNew();
    }

    /**
     * 동기화 완료 처리 (PENDING -> COMPLETED).
     *
     * @param now 완료 시각
     */
    public void complete(Instant now) {
        this.status = ImageVariantSyncOutboxStatus.COMPLETED;
        this.processedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /**
     * 동기화 실패 처리.
     *
     * <p>재시도 횟수를 증가시키고, 최대 재시도 횟수 미만이면 PENDING 상태를 유지합니다. 최대 재시도 횟수 이상이면 FAILED 상태로 변경합니다.
     *
     * @param errorMessage 에러 메시지
     * @param now 현재 시각
     */
    public void fail(String errorMessage, Instant now) {
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;

        if (this.retryCount >= this.maxRetry) {
            this.status = ImageVariantSyncOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = ImageVariantSyncOutboxStatus.PENDING;
        }
    }

    /**
     * 영속화 후 JPA가 증가시킨 버전을 도메인 객체에 반영합니다.
     *
     * @param version 저장 후 갱신된 버전
     */
    public void refreshVersion(long version) {
        this.version = version;
    }

    /** PENDING 상태 여부. */
    public boolean isPending() {
        return status.isPending();
    }

    /** COMPLETED 상태 여부. */
    public boolean isCompleted() {
        return status.isCompleted();
    }

    // Getters
    public ImageVariantSyncOutboxId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long sourceImageId() {
        return sourceImageId;
    }

    public ImageSourceType sourceType() {
        return sourceType;
    }

    public ImageVariantSyncOutboxStatus status() {
        return status;
    }

    public int retryCount() {
        return retryCount;
    }

    public int maxRetry() {
        return maxRetry;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant processedAt() {
        return processedAt;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public long version() {
        return version;
    }
}
