package com.ryuqq.marketplace.domain.productintelligence.aggregate;

import com.ryuqq.marketplace.domain.productintelligence.exception.InvalidOutboxStateException;
import com.ryuqq.marketplace.domain.productintelligence.id.IntelligenceOutboxId;
import com.ryuqq.marketplace.domain.productintelligence.vo.IntelligenceOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.productintelligence.vo.IntelligenceOutboxStatus;
import java.time.Instant;

/**
 * Intelligence Pipeline Outbox Aggregate.
 *
 * <p>Outbox Relay 기반 SQS 발행 구조로 동작합니다:
 *
 * <ul>
 *   <li>Outbox Relay (Scheduler): PENDING → SENT (3개 Analyzer 큐 발행)
 *   <li>발행 성공: SENT → COMPLETED
 *   <li>발행 실패: retry → PENDING 복귀 또는 max retry 초과 → FAILED
 * </ul>
 *
 * <p><strong>동시성 제어</strong>:
 *
 * <ul>
 *   <li>version: 낙관적 락을 위한 버전 필드
 *   <li>updatedAt: 좀비 상태 감지를 위한 갱신 시각
 *   <li>idempotencyKey: 중복 분석 요청 방지
 * </ul>
 */
public class IntelligenceOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final IntelligenceOutboxId id;
    private final Long productGroupId;
    private Long profileId;
    private IntelligenceOutboxStatus status;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final IntelligenceOutboxIdempotencyKey idempotencyKey;

    private IntelligenceOutbox(
            IntelligenceOutboxId id,
            Long productGroupId,
            Long profileId,
            IntelligenceOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            IntelligenceOutboxIdempotencyKey idempotencyKey) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.profileId = profileId;
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

    public static IntelligenceOutbox forNew(Long productGroupId, Instant now) {
        IntelligenceOutboxIdempotencyKey idempotencyKey =
                IntelligenceOutboxIdempotencyKey.generate(productGroupId, now);
        return new IntelligenceOutbox(
                IntelligenceOutboxId.forNew(),
                productGroupId,
                null,
                IntelligenceOutboxStatus.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                idempotencyKey);
    }

    public static IntelligenceOutbox reconstitute(
            IntelligenceOutboxId id,
            Long productGroupId,
            Long profileId,
            IntelligenceOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new IntelligenceOutbox(
                id,
                productGroupId,
                profileId,
                status,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version,
                IntelligenceOutboxIdempotencyKey.of(idempotencyKey));
    }

    public boolean isNew() {
        return id.isNew();
    }

    /**
     * ProfileId 할당. Orchestration에서 ProductProfile 생성 후 호출.
     *
     * @param profileId 할당할 프로파일 ID
     */
    public void assignProfile(Long profileId) {
        this.profileId = profileId;
    }

    /**
     * SQS 발행 완료 (PENDING → SENT). Outbox Relay에서 사용.
     *
     * @param now 현재 시각
     */
    public void markAsSent(Instant now) {
        assertStatus(IntelligenceOutboxStatus.PENDING, "SENT로 전환");
        this.status = IntelligenceOutboxStatus.SENT;
        this.updatedAt = now;
    }

    /**
     * 발행 완료 (SENT → COMPLETED). 3개 Analyzer 큐 발행 성공 시 호출.
     *
     * @param now 현재 시각
     */
    public void complete(Instant now) {
        assertStatus(IntelligenceOutboxStatus.SENT, "COMPLETED로 전환");
        this.status = IntelligenceOutboxStatus.COMPLETED;
        this.processedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /**
     * 실패 결과를 반영합니다.
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
     * 진행 중 상태에서 타임아웃으로 PENDING 복구.
     *
     * @param now 현재 시각
     */
    public void recoverFromTimeout(Instant now) {
        if (!status.isInProgress()) {
            throw new InvalidOutboxStateException(status.name(), "타임아웃 복구");
        }
        this.status = IntelligenceOutboxStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구 (이전 상태: " + status + ")";
    }

    /**
     * 현재 상태가 기대 상태인지 확인합니다. CAS(Compare-And-Swap) 기반 멱등성 보장에 사용.
     *
     * @param expected 기대 상태
     * @return 현재 상태가 기대 상태와 일치하면 true
     */
    public boolean hasExpectedStatus(IntelligenceOutboxStatus expected) {
        return this.status == expected;
    }

    private void failAndRetry(String errorMessage, Instant now) {
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;

        if (this.retryCount >= this.maxRetry) {
            this.status = IntelligenceOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = IntelligenceOutboxStatus.PENDING;
        }
    }

    private void fail(String errorMessage, Instant now) {
        this.status = IntelligenceOutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = now;
        this.updatedAt = now;
    }

    private void assertStatus(IntelligenceOutboxStatus expected, String action) {
        if (this.status != expected) {
            throw new InvalidOutboxStateException(status.name(), action);
        }
    }

    /** 재시도 가능 여부. */
    public boolean canRetry() {
        return retryCount < maxRetry && status.canSend();
    }

    // Getters
    public IntelligenceOutboxId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long productGroupId() {
        return productGroupId;
    }

    public Long profileId() {
        return profileId;
    }

    public IntelligenceOutboxStatus status() {
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

    public IntelligenceOutboxIdempotencyKey idempotencyKey() {
        return idempotencyKey;
    }

    public String idempotencyKeyValue() {
        return idempotencyKey.value();
    }
}
