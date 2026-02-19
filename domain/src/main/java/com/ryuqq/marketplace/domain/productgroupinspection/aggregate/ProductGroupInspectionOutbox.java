package com.ryuqq.marketplace.domain.productgroupinspection.aggregate;

import com.ryuqq.marketplace.domain.productgroupinspection.id.InspectionOutboxId;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionOutboxStatus;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionResult;
import java.time.Instant;

/**
 * 상품 그룹 검수 Outbox Aggregate.
 *
 * <p>SQS 기반 3단계 파이프라인 구조로 동작합니다:
 *
 * <ul>
 *   <li>Outbox Relay (Scheduler): PENDING → SENT (SQS 발행)
 *   <li>Scoring Consumer: SENT → SCORING → ENHANCING or VERIFYING
 *   <li>Enhancement Consumer: ENHANCING → VERIFYING
 *   <li>Verification Consumer: VERIFYING → COMPLETED or FAILED
 * </ul>
 *
 * <p><strong>동시성 제어</strong>:
 *
 * <ul>
 *   <li>version: 낙관적 락을 위한 버전 필드
 *   <li>updatedAt: 좀비 상태 감지를 위한 갱신 시각
 *   <li>idempotencyKey: 중복 검수 요청 방지
 * </ul>
 */
public class ProductGroupInspectionOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final InspectionOutboxId id;
    private final Long productGroupId;
    private InspectionOutboxStatus status;
    private String inspectionResultJson;
    private Integer totalScore;
    private Boolean passed;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final InspectionOutboxIdempotencyKey idempotencyKey;

    private ProductGroupInspectionOutbox(
            InspectionOutboxId id,
            Long productGroupId,
            InspectionOutboxStatus status,
            String inspectionResultJson,
            Integer totalScore,
            Boolean passed,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            InspectionOutboxIdempotencyKey idempotencyKey) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.status = status;
        this.inspectionResultJson = inspectionResultJson;
        this.totalScore = totalScore;
        this.passed = passed;
        this.retryCount = retryCount;
        this.maxRetry = maxRetry;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
        this.errorMessage = errorMessage;
        this.version = version;
        this.idempotencyKey = idempotencyKey;
    }

    public static ProductGroupInspectionOutbox forNew(Long productGroupId, Instant now) {
        InspectionOutboxIdempotencyKey idempotencyKey =
                InspectionOutboxIdempotencyKey.generate(productGroupId, now);
        return new ProductGroupInspectionOutbox(
                InspectionOutboxId.forNew(),
                productGroupId,
                InspectionOutboxStatus.PENDING,
                null,
                null,
                null,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                idempotencyKey);
    }

    public static ProductGroupInspectionOutbox reconstitute(
            InspectionOutboxId id,
            Long productGroupId,
            InspectionOutboxStatus status,
            String inspectionResultJson,
            Integer totalScore,
            Boolean passed,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new ProductGroupInspectionOutbox(
                id,
                productGroupId,
                status,
                inspectionResultJson,
                totalScore,
                passed,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version,
                InspectionOutboxIdempotencyKey.of(idempotencyKey));
    }

    public boolean isNew() {
        return id.isNew();
    }

    /**
     * 처리 시작 (PENDING → SENT). Processor/Relay에서 사용.
     *
     * @param now 현재 시각
     */
    public void startProcessing(Instant now) {
        markAsSent(now);
    }

    /**
     * SQS 발행 완료 (PENDING → SENT). Outbox Relay에서 사용.
     *
     * @param now 현재 시각
     */
    public void markAsSent(Instant now) {
        assertStatus(InspectionOutboxStatus.PENDING, "SENT로 전환");
        this.status = InspectionOutboxStatus.SENT;
        this.updatedAt = now;
    }

    /**
     * Scoring 시작 (SENT → SCORING). ScoringConsumer에서 사용.
     *
     * @param now 현재 시각
     */
    public void startScoring(Instant now) {
        assertStatus(InspectionOutboxStatus.SENT, "SCORING으로 전환");
        this.status = InspectionOutboxStatus.SCORING;
        this.updatedAt = now;
    }

    /**
     * Enhancement 시작 (SCORING → ENHANCING). 점수 미달 시 ScoringConsumer에서 사용.
     *
     * @param now 현재 시각
     */
    public void startEnhancing(Instant now) {
        assertStatus(InspectionOutboxStatus.SCORING, "ENHANCING으로 전환");
        this.status = InspectionOutboxStatus.ENHANCING;
        this.updatedAt = now;
    }

    /**
     * Verification 시작. SCORING → VERIFYING (점수 통과) 또는 ENHANCING → VERIFYING (보강 완료).
     *
     * @param now 현재 시각
     */
    public void startVerifying(Instant now) {
        if (status != InspectionOutboxStatus.SCORING
                && status != InspectionOutboxStatus.ENHANCING) {
            throw new IllegalStateException(
                    "VERIFYING으로 전환은 SCORING 또는 ENHANCING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = InspectionOutboxStatus.VERIFYING;
        this.updatedAt = now;
    }

    /**
     * 검수 완료 (VERIFYING → COMPLETED). 결과 저장.
     *
     * @param result 검수 결과
     * @param resultJson 검수 결과 JSON
     * @param now 현재 시각
     */
    public void complete(InspectionResult result, String resultJson, Instant now) {
        assertStatus(InspectionOutboxStatus.VERIFYING, "COMPLETED로 전환");
        this.status = InspectionOutboxStatus.COMPLETED;
        this.inspectionResultJson = resultJson;
        this.totalScore = result.totalScore();
        this.passed = result.passed();
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
            throw new IllegalStateException("타임아웃 복구는 진행 중 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = InspectionOutboxStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구 (이전 상태: " + status + ")";
    }

    /**
     * 현재 상태가 기대 상태인지 확인합니다. CAS(Compare-And-Swap) 기반 멱등성 보장에 사용.
     *
     * @param expected 기대 상태
     * @return 현재 상태가 기대 상태와 일치하면 true
     */
    public boolean hasExpectedStatus(InspectionOutboxStatus expected) {
        return this.status == expected;
    }

    /**
     * Scoring 결과 저장 (중간 결과). complete와 달리 상태 전환 없이 점수만 저장.
     *
     * @param result 검수 결과
     * @param resultJson 결과 JSON
     */
    public void saveScoringResult(InspectionResult result, String resultJson) {
        this.inspectionResultJson = resultJson;
        this.totalScore = result.totalScore();
        this.passed = result.passed();
    }

    private void failAndRetry(String errorMessage, Instant now) {
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;

        if (this.retryCount >= this.maxRetry) {
            this.status = InspectionOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = InspectionOutboxStatus.PENDING;
        }
    }

    private void fail(String errorMessage, Instant now) {
        this.status = InspectionOutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = now;
        this.updatedAt = now;
    }

    private void assertStatus(InspectionOutboxStatus expected, String action) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    action + "은 " + expected + " 상태에서만 가능합니다. 현재 상태: " + status);
        }
    }

    /** 재시도 가능 여부. */
    public boolean canRetry() {
        return retryCount < maxRetry && status.canSend();
    }

    // Getters
    public InspectionOutboxId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long productGroupId() {
        return productGroupId;
    }

    public InspectionOutboxStatus status() {
        return status;
    }

    public String inspectionResultJson() {
        return inspectionResultJson;
    }

    public Integer totalScore() {
        return totalScore;
    }

    public Boolean passed() {
        return passed;
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

    public InspectionOutboxIdempotencyKey idempotencyKey() {
        return idempotencyKey;
    }

    public String idempotencyKeyValue() {
        return idempotencyKey.value();
    }

    public boolean isPending() {
        return status.isPending();
    }

    public boolean isSent() {
        return status.isSent();
    }

    public boolean isScoring() {
        return status.isScoring();
    }

    public boolean isEnhancing() {
        return status.isEnhancing();
    }

    public boolean isVerifying() {
        return status.isVerifying();
    }

    public boolean isCompleted() {
        return status.isCompleted();
    }

    public boolean isFailed() {
        return status.isFailed();
    }
}
