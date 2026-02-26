package com.ryuqq.marketplace.domain.legacyconversion.aggregate;

import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyConversionOutboxId;
import com.ryuqq.marketplace.domain.legacyconversion.vo.LegacyConversionOutboxStatus;
import java.time.Instant;

/**
 * 레거시 변환 Outbox Aggregate.
 *
 * <p>레거시 상품 등록 시 생성되며, 스케줄러에 의해 내부 상품으로 변환됩니다. payload 없이 legacyProductGroupId만 저장하고, 변환 시점에
 * luxurydb에서 최신 데이터를 조회합니다.
 *
 * <p><strong>동시성 제어</strong>:
 *
 * <ul>
 *   <li>version: 낙관적 락을 위한 버전 필드
 *   <li>updatedAt: PROCESSING 좀비 상태 감지를 위한 갱신 시각
 * </ul>
 */
public class LegacyConversionOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final LegacyConversionOutboxId id;
    private final long legacyProductGroupId;
    private LegacyConversionOutboxStatus status;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;

    private LegacyConversionOutbox(
            LegacyConversionOutboxId id,
            long legacyProductGroupId,
            LegacyConversionOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version) {
        this.id = id;
        this.legacyProductGroupId = legacyProductGroupId;
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
     * @param legacyProductGroupId 레거시 상품그룹 ID
     * @param now 현재 시각
     * @return 새 LegacyConversionOutbox 인스턴스
     */
    public static LegacyConversionOutbox forNew(long legacyProductGroupId, Instant now) {
        return new LegacyConversionOutbox(
                LegacyConversionOutboxId.forNew(),
                legacyProductGroupId,
                LegacyConversionOutboxStatus.PENDING,
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
     * @param legacyProductGroupId 레거시 상품그룹 ID
     * @param status 상태
     * @param retryCount 재시도 횟수
     * @param maxRetry 최대 재시도 횟수
     * @param createdAt 생성일시
     * @param updatedAt 갱신일시
     * @param processedAt 처리일시
     * @param errorMessage 에러 메시지
     * @param version 낙관적 락 버전
     * @return 재구성된 LegacyConversionOutbox 인스턴스
     */
    public static LegacyConversionOutbox reconstitute(
            LegacyConversionOutboxId id,
            long legacyProductGroupId,
            LegacyConversionOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version) {
        return new LegacyConversionOutbox(
                id,
                legacyProductGroupId,
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
     * 처리 시작.
     *
     * @param now 현재 시각
     */
    public void startProcessing(Instant now) {
        if (!status.canProcess()) {
            throw new IllegalStateException("처리할 수 없는 상태입니다. 현재 상태: " + status);
        }
        this.status = LegacyConversionOutboxStatus.PROCESSING;
        this.updatedAt = now;
    }

    /**
     * 처리 완료.
     *
     * @param now 처리 완료 시각
     */
    public void complete(Instant now) {
        this.status = LegacyConversionOutboxStatus.COMPLETED;
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
            this.status = LegacyConversionOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = LegacyConversionOutboxStatus.PENDING;
        }
    }

    /**
     * PROCESSING 상태에서 타임아웃으로 복구.
     *
     * @param now 현재 시각
     */
    public void recoverFromTimeout(Instant now) {
        if (this.status != LegacyConversionOutboxStatus.PROCESSING) {
            throw new IllegalStateException("타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = LegacyConversionOutboxStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구";
    }

    /** 재시도 가능 여부. */
    public boolean canRetry() {
        return retryCount < maxRetry && status.canProcess();
    }

    // Getters
    public LegacyConversionOutboxId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public long legacyProductGroupId() {
        return legacyProductGroupId;
    }

    public LegacyConversionOutboxStatus status() {
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
