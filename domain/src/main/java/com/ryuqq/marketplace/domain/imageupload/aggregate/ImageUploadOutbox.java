package com.ryuqq.marketplace.domain.imageupload.aggregate;

import com.ryuqq.marketplace.domain.imageupload.id.ImageUploadOutboxId;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxStatus;
import com.ryuqq.marketplace.domain.imageupload.vo.OriginUrl;
import java.time.Instant;
import java.util.Locale;

/**
 * 이미지 업로드 Outbox Aggregate.
 *
 * <p>ProductGroup/Description 이미지를 S3에 비동기 업로드하기 위한 Outbox 패턴 구현체입니다.
 *
 * <p>이미지 저장 시 생성되며, 스케줄러에 의해 처리됩니다. FileStorageClient.downloadFromExternalUrl()을 호출하여 원본 URL에서 다운로드
 * 후 S3에 업로드합니다.
 *
 * <p><strong>동시성 제어</strong>:
 *
 * <ul>
 *   <li>version: 낙관적 락을 위한 버전 필드
 *   <li>updatedAt: PROCESSING 좀비 상태 감지를 위한 갱신 시각
 *   <li>idempotencyKey: 중복 업로드 방지
 * </ul>
 *
 * <p><strong>2-스케줄러 패턴</strong>:
 *
 * <ul>
 *   <li>process-pending: FileFlow 다운로드 태스크 생성 (논블로킹)
 *   <li>poll-processing: 다운로드 태스크 완료 여부 확인 (논블로킹)
 * </ul>
 */
public class ImageUploadOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final ImageUploadOutboxId id;
    private final Long sourceId;
    private final ImageSourceType sourceType;
    private final OriginUrl originUrl;
    private ImageUploadOutboxStatus status;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final ImageUploadOutboxIdempotencyKey idempotencyKey;
    private String downloadTaskId;

    private ImageUploadOutbox(
            ImageUploadOutboxId id,
            Long sourceId,
            ImageSourceType sourceType,
            OriginUrl originUrl,
            ImageUploadOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            ImageUploadOutboxIdempotencyKey idempotencyKey,
            String downloadTaskId) {
        this.id = id;
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.originUrl = originUrl;
        this.status = status;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
        this.idempotencyKey = idempotencyKey;
        this.downloadTaskId = downloadTaskId;
    }

    /**
     * 새 Outbox 생성.
     *
     * @param sourceId 이미지 DB ID
     * @param sourceType 이미지 소스 타입
     * @param originUrl 다운로드할 원본 URL
     * @param now 현재 시각
     * @return 새 ImageUploadOutbox 인스턴스
     */
    public static ImageUploadOutbox forNew(
            Long sourceId, ImageSourceType sourceType, String originUrl, Instant now) {
        ImageUploadOutboxIdempotencyKey idempotencyKey =
                ImageUploadOutboxIdempotencyKey.generate(sourceType, sourceId, now);
        return new ImageUploadOutbox(
                ImageUploadOutboxId.forNew(),
                sourceId,
                sourceType,
                OriginUrl.of(originUrl),
                ImageUploadOutboxStatus.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                idempotencyKey,
                null);
    }

    /**
     * DB에서 재구성.
     *
     * @param id Outbox ID
     * @param sourceId 이미지 DB ID
     * @param sourceType 이미지 소스 타입
     * @param originUrl 원본 URL
     * @param status 상태
     * @param retryCount 재시도 횟수
     * @param maxRetry 최대 재시도 횟수
     * @param createdAt 생성일시
     * @param updatedAt 갱신일시
     * @param processedAt 처리일시
     * @param errorMessage 에러 메시지
     * @param version 낙관적 락 버전
     * @param idempotencyKey 멱등키 (String)
     * @param downloadTaskId FileFlow 다운로드 태스크 ID (null 가능)
     * @return 재구성된 ImageUploadOutbox 인스턴스
     */
    public static ImageUploadOutbox reconstitute(
            ImageUploadOutboxId id,
            Long sourceId,
            ImageSourceType sourceType,
            String originUrl,
            ImageUploadOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey,
            String downloadTaskId) {
        return new ImageUploadOutbox(
                id,
                sourceId,
                sourceType,
                OriginUrl.of(originUrl),
                status,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version,
                ImageUploadOutboxIdempotencyKey.of(idempotencyKey),
                downloadTaskId);
    }

    public boolean isNew() {
        return id.isNew();
    }

    /**
     * 처리 시작 (다운로드 태스크 생성 후 호출).
     *
     * <p>PENDING 또는 PROCESSING 상태에서만 호출 가능합니다.
     *
     * @param now 현재 시각 (updatedAt 갱신용)
     * @param downloadTaskId FileFlow 다운로드 태스크 ID
     */
    public void startProcessing(Instant now, String downloadTaskId) {
        if (!status.canProcess()) {
            throw new IllegalStateException("처리할 수 없는 상태입니다. 현재 상태: " + status);
        }
        this.status = ImageUploadOutboxStatus.PROCESSING;
        this.updatedAt = now;
        this.downloadTaskId = downloadTaskId;
    }

    /**
     * 처리 완료.
     *
     * @param now 처리 완료 시각
     */
    public void complete(Instant now) {
        this.status = ImageUploadOutboxStatus.COMPLETED;
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
            this.status = ImageUploadOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = ImageUploadOutboxStatus.PENDING;
        }
    }

    /**
     * 즉시 실패 처리 (재시도 없이).
     *
     * @param errorMessage 에러 메시지
     * @param now 현재 시각
     */
    public void fail(String errorMessage, Instant now) {
        this.status = ImageUploadOutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = now;
        this.updatedAt = now;
    }

    /**
     * 실패 결과를 반영합니다.
     *
     * <p>canRetry=true이면 failAndRetry()를 호출하여 재시도 처리하고 (maxRetry 초과 시 자동 FAILED), canRetry=false이면
     * 즉시 FAILED 상태로 변경합니다.
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
        if (this.status != ImageUploadOutboxStatus.PROCESSING) {
            throw new IllegalStateException("타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = ImageUploadOutboxStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구";
        this.downloadTaskId = null;
    }

    /**
     * 외부 서비스 장애 시 retry 보호를 위한 지연 복귀.
     *
     * <p>retryCount를 증가시키지 않고 PENDING으로 복귀합니다. 알려진 장애 상황에서 retry 횟수 소진을 방지합니다.
     *
     * @param now 현재 시각
     */
    public void deferRetry(Instant now) {
        this.status = ImageUploadOutboxStatus.PENDING;
        this.updatedAt = now;
        this.downloadTaskId = null;
    }

    /**
     * FAILED 상태에서 복구를 위해 초기화합니다.
     *
     * <p>FAILED → PENDING 전이, retryCount=0, downloadTaskId=null로 초기화합니다.
     *
     * @param now 현재 시각
     */
    public void resetForRetry(Instant now) {
        if (this.status != ImageUploadOutboxStatus.FAILED) {
            throw new IllegalStateException("복구는 FAILED 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = ImageUploadOutboxStatus.PENDING;
        this.retryCount = 0;
        this.updatedAt = now;
        this.processedAt = null;
        this.errorMessage = null;
        this.downloadTaskId = null;
    }

    /**
     * 복구 가능한 실패인지 판단합니다.
     *
     * <p>잘못된 요청(BadRequest) 에러는 재시도해도 동일하게 실패하므로 복구 불가로 판단합니다.
     *
     * @return 복구 가능 여부
     */
    public boolean isRecoverableFailure() {
        if (this.status != ImageUploadOutboxStatus.FAILED) {
            return false;
        }
        if (this.errorMessage == null) {
            return true;
        }
        return !this.errorMessage.contains("잘못된 요청");
    }

    /**
     * PROCESSING 타임아웃 여부 확인.
     *
     * @param now 현재 시각
     * @param timeoutSeconds 타임아웃 시간(초)
     * @return 타임아웃 여부
     */
    public boolean isProcessingTimeout(Instant now, long timeoutSeconds) {
        if (this.status != ImageUploadOutboxStatus.PROCESSING) {
            return false;
        }
        Instant timeoutThreshold = this.updatedAt.plusSeconds(timeoutSeconds);
        return timeoutThreshold.isBefore(now);
    }

    /** 재시도 가능 여부. */
    public boolean canRetry() {
        return retryCount < maxRetry && status.canProcess();
    }

    /** 처리 대상 여부 (PENDING 상태). */
    public boolean shouldProcess() {
        return status.isPending();
    }

    /**
     * S3 업로드용 파일명을 생성합니다.
     *
     * <p>형식: {@code {sourceType}_{sourceId}_{epochMilli}{extension}}
     *
     * @param now 현재 시각 (파일명 유니크성 보장)
     * @return 생성된 파일명 (예: "product_group_image_123_1706612400000.jpg")
     */
    public String generateFilename(Instant now) {
        String typeName = sourceType.name();
        String lowerTypeName = typeName.toLowerCase(Locale.ROOT);
        String extension = originUrl.extension();
        return lowerTypeName + "_" + sourceId + "_" + now.toEpochMilli() + extension;
    }

    // Getters
    public ImageUploadOutboxId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long sourceId() {
        return sourceId;
    }

    public ImageSourceType sourceType() {
        return sourceType;
    }

    public OriginUrl originUrl() {
        return originUrl;
    }

    public String originUrlValue() {
        return originUrl.value();
    }

    public ImageUploadOutboxStatus status() {
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

    /**
     * 영속화 후 JPA가 증가시킨 버전을 도메인 객체에 반영합니다.
     *
     * @param version 저장 후 갱신된 버전
     */
    public void refreshVersion(long version) {
        this.version = version;
    }

    public ImageUploadOutboxIdempotencyKey idempotencyKey() {
        return idempotencyKey;
    }

    public String idempotencyKeyValue() {
        return idempotencyKey.value();
    }

    public String downloadTaskId() {
        return downloadTaskId;
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
