package com.ryuqq.marketplace.domain.imagetransform.aggregate;

import com.ryuqq.marketplace.domain.imagetransform.id.ImageTransformOutboxId;
import com.ryuqq.marketplace.domain.imagetransform.vo.ImageTransformOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.imagetransform.vo.ImageTransformOutboxStatus;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;

/**
 * 이미지 변환 Outbox Aggregate.
 *
 * <p>이미지 업로드 완료 후 멀티 사이즈 WEBP 변환을 비동기로 처리하기 위한 Outbox 패턴 구현체입니다.
 *
 * <p>2-스케줄러 구조로 동작합니다:
 *
 * <ul>
 *   <li>Scheduler 1 (ProcessPending): PENDING → TransformRequestApi.create() → PROCESSING
 *   <li>Scheduler 2 (PollProcessing): PROCESSING → TransformRequestApi.get() → COMPLETED/FAILED
 * </ul>
 *
 * <p><strong>동시성 제어</strong>:
 *
 * <ul>
 *   <li>version: 낙관적 락을 위한 버전 필드
 *   <li>updatedAt: PROCESSING 좀비 상태 감지를 위한 갱신 시각
 *   <li>idempotencyKey: 중복 변환 요청 방지
 * </ul>
 */
public class ImageTransformOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final ImageTransformOutboxId id;
    private final Long sourceImageId;
    private final ImageSourceType sourceType;
    private final ImageUrl uploadedUrl;
    private final ImageVariantType variantType;
    private String transformRequestId;
    private ImageTransformOutboxStatus status;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final ImageTransformOutboxIdempotencyKey idempotencyKey;

    private ImageTransformOutbox(
            ImageTransformOutboxId id,
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageUrl uploadedUrl,
            ImageVariantType variantType,
            String transformRequestId,
            ImageTransformOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            ImageTransformOutboxIdempotencyKey idempotencyKey) {
        this.id = id;
        this.sourceImageId = sourceImageId;
        this.sourceType = sourceType;
        this.uploadedUrl = uploadedUrl;
        this.variantType = variantType;
        this.transformRequestId = transformRequestId;
        this.status = status;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
        this.idempotencyKey = idempotencyKey;
    }

    /**
     * 새 Outbox 생성.
     *
     * @param sourceImageId 소스 이미지 DB ID
     * @param sourceType 이미지 소스 타입
     * @param uploadedUrl 업로드된 CDN URL
     * @param variantType 변환 대상 Variant 타입
     * @param now 현재 시각
     * @return 새 ImageTransformOutbox 인스턴스
     */
    public static ImageTransformOutbox forNew(
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageUrl uploadedUrl,
            ImageVariantType variantType,
            Instant now) {
        ImageTransformOutboxIdempotencyKey idempotencyKey =
                ImageTransformOutboxIdempotencyKey.generate(sourceImageId, variantType, now);
        return new ImageTransformOutbox(
                ImageTransformOutboxId.forNew(),
                sourceImageId,
                sourceType,
                uploadedUrl,
                variantType,
                null,
                ImageTransformOutboxStatus.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                idempotencyKey);
    }

    /**
     * DB에서 재구성.
     *
     * @param id Outbox ID
     * @param sourceImageId 소스 이미지 DB ID
     * @param sourceType 이미지 소스 타입
     * @param uploadedUrl 업로드된 CDN URL
     * @param variantType Variant 타입
     * @param transformRequestId FileFlow 변환 요청 ID
     * @param status 상태
     * @param retryCount 재시도 횟수
     * @param maxRetry 최대 재시도 횟수
     * @param createdAt 생성일시
     * @param updatedAt 갱신일시
     * @param processedAt 처리일시
     * @param errorMessage 에러 메시지
     * @param version 낙관적 락 버전
     * @param idempotencyKey 멱등키 (String)
     * @return 재구성된 ImageTransformOutbox 인스턴스
     */
    public static ImageTransformOutbox reconstitute(
            ImageTransformOutboxId id,
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageUrl uploadedUrl,
            ImageVariantType variantType,
            String transformRequestId,
            ImageTransformOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new ImageTransformOutbox(
                id,
                sourceImageId,
                sourceType,
                uploadedUrl,
                variantType,
                transformRequestId,
                status,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version,
                ImageTransformOutboxIdempotencyKey.of(idempotencyKey));
    }

    public boolean isNew() {
        return id.isNew();
    }

    /**
     * 처리 시작 (PENDING → PROCESSING).
     *
     * <p>TransformRequestApi.create() 호출 전 상태 변경에 사용합니다. transformRequestId는 API 호출 후 설정합니다.
     *
     * @param now 현재 시각
     * @param transformRequestId FileFlow 변환 요청 ID (최초 호출 시 null 가능)
     */
    public void startProcessing(Instant now, String transformRequestId) {
        if (!status.canProcess()) {
            throw new IllegalStateException("처리할 수 없는 상태입니다. 현재 상태: " + status);
        }
        this.status = ImageTransformOutboxStatus.PROCESSING;
        this.transformRequestId = transformRequestId;
        this.updatedAt = now;
    }

    /**
     * 처리 완료 (PROCESSING → COMPLETED).
     *
     * @param now 처리 완료 시각
     */
    public void complete(Instant now) {
        this.status = ImageTransformOutboxStatus.COMPLETED;
        this.processedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /**
     * 처리 실패 및 재시도.
     *
     * <p>재시도 횟수를 증가시키고, 최대 재시도 횟수 초과 시 FAILED 상태로 변경합니다.
     *
     * @param errorMessage 에러 메시지
     * @param now 현재 시각
     */
    public void failAndRetry(String errorMessage, Instant now) {
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;

        if (this.retryCount >= this.maxRetry) {
            this.status = ImageTransformOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = ImageTransformOutboxStatus.PENDING;
        }
    }

    /**
     * 즉시 실패 처리 (재시도 없이).
     *
     * @param errorMessage 에러 메시지
     * @param now 현재 시각
     */
    public void fail(String errorMessage, Instant now) {
        this.status = ImageTransformOutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = now;
        this.updatedAt = now;
    }

    /**
     * 실패 결과를 반영합니다.
     *
     * <p>canRetry=true이면 failAndRetry()를 호출하고, canRetry=false이면 즉시 FAILED 상태로 변경합니다.
     *
     * @param canRetry 재시도 가능 여부
     * @param errorMessage 에러 메시지
     * @param now 현재 시각
     */
    public void recordFailure(boolean canRetry, String errorMessage, Instant now) {
        if (canRetry) {
            failAndRetry(errorMessage, now);
        } else {
            fail(errorMessage, now);
        }
    }

    /**
     * PROCESSING 상태에서 타임아웃으로 복구.
     *
     * <p>updatedAt이 특정 시간 이전이면 좀비 상태로 간주하고 PENDING으로 복구합니다.
     *
     * @param now 현재 시각
     */
    public void recoverFromTimeout(Instant now) {
        if (this.status != ImageTransformOutboxStatus.PROCESSING) {
            throw new IllegalStateException("타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = ImageTransformOutboxStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구";
    }

    /**
     * PROCESSING 타임아웃 여부 확인.
     *
     * @param now 현재 시각
     * @param timeoutSeconds 타임아웃 시간(초)
     * @return 타임아웃 여부
     */
    public boolean isProcessingTimeout(Instant now, long timeoutSeconds) {
        if (this.status != ImageTransformOutboxStatus.PROCESSING) {
            return false;
        }
        Instant timeoutThreshold = this.updatedAt.plusSeconds(timeoutSeconds);
        return timeoutThreshold.isBefore(now);
    }

    /** 재시도 가능 여부. */
    public boolean canRetry() {
        return retryCount < maxRetry && status.canProcess();
    }

    // Getters
    public ImageTransformOutboxId id() {
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

    public ImageUrl uploadedUrl() {
        return uploadedUrl;
    }

    public String uploadedUrlValue() {
        return uploadedUrl.value();
    }

    public ImageVariantType variantType() {
        return variantType;
    }

    public String transformRequestId() {
        return transformRequestId;
    }

    public ImageTransformOutboxStatus status() {
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

    public ImageTransformOutboxIdempotencyKey idempotencyKey() {
        return idempotencyKey;
    }

    public String idempotencyKeyValue() {
        return idempotencyKey.value();
    }

    public boolean isPending() {
        return status.isPending();
    }

    public boolean isProcessing() {
        return status.isProcessing();
    }

    public boolean isCompleted() {
        return status.isCompleted();
    }

    public boolean isFailed() {
        return status.isFailed();
    }
}
