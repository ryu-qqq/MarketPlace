package com.ryuqq.marketplace.domain.seller.aggregate;

import com.ryuqq.marketplace.domain.seller.id.SellerAuthOutboxId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.SellerAuthOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.seller.vo.SellerAuthOutboxStatus;
import java.time.Instant;

/**
 * 셀러 인증 Outbox Aggregate.
 *
 * <p>인증 서버(테넌트/조직 등록) 연동을 위한 Outbox 패턴 구현체입니다.
 *
 * <p>셀러 승인 시 생성되며, 비동기 이벤트 리스너 또는 스케줄러에 의해 처리됩니다. 한 번의 호출로 테넌트와 조직을 함께 생성합니다. 처리 실패 시 최대 재시도 횟수까지
 * 재시도합니다.
 *
 * <p><strong>동시성 제어</strong>:
 *
 * <ul>
 *   <li>version: 낙관적 락을 위한 버전 필드
 *   <li>updatedAt: PROCESSING 좀비 상태 감지를 위한 갱신 시각
 *   <li>idempotencyKey: 외부 API 호출 멱등성 보장
 * </ul>
 */
public class SellerAuthOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final SellerAuthOutboxId id;
    private SellerId sellerId;
    private final String payload;
    private SellerAuthOutboxStatus status;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final SellerAuthOutboxIdempotencyKey idempotencyKey;

    private SellerAuthOutbox(
            SellerAuthOutboxId id,
            SellerId sellerId,
            String payload,
            SellerAuthOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            SellerAuthOutboxIdempotencyKey idempotencyKey) {
        this.id = id;
        this.sellerId = sellerId;
        this.payload = payload;
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
     * SellerId 없이 새 Outbox 생성.
     *
     * <p>SellerId는 나중에 assignSellerId()로 설정합니다.
     *
     * @param payload JSON 페이로드
     * @param now 현재 시각
     * @return 새 SellerAuthOutbox 인스턴스
     */
    public static SellerAuthOutbox forNew(String payload, Instant now) {
        SellerAuthOutboxIdempotencyKey idempotencyKey =
                SellerAuthOutboxIdempotencyKey.generate(null, now);
        return new SellerAuthOutbox(
                SellerAuthOutboxId.forNew(),
                null,
                payload,
                SellerAuthOutboxStatus.PENDING,
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
     * 새 Outbox 생성.
     *
     * @param sellerId 셀러 ID
     * @param payload JSON 페이로드
     * @param now 현재 시각
     * @return 새 SellerAuthOutbox 인스턴스
     */
    public static SellerAuthOutbox forNew(SellerId sellerId, String payload, Instant now) {
        SellerAuthOutboxIdempotencyKey idempotencyKey =
                SellerAuthOutboxIdempotencyKey.generate(sellerId, now);
        return new SellerAuthOutbox(
                SellerAuthOutboxId.forNew(),
                sellerId,
                payload,
                SellerAuthOutboxStatus.PENDING,
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
     * 새 Outbox 생성 (최대 재시도 횟수 지정).
     *
     * @param sellerId 셀러 ID
     * @param payload JSON 페이로드
     * @param maxRetry 최대 재시도 횟수
     * @param now 현재 시각
     * @return 새 SellerAuthOutbox 인스턴스
     */
    public static SellerAuthOutbox forNew(
            SellerId sellerId, String payload, int maxRetry, Instant now) {
        SellerAuthOutboxIdempotencyKey idempotencyKey =
                SellerAuthOutboxIdempotencyKey.generate(sellerId, now);
        return new SellerAuthOutbox(
                SellerAuthOutboxId.forNew(),
                sellerId,
                payload,
                SellerAuthOutboxStatus.PENDING,
                0,
                maxRetry,
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
     * @param sellerId 셀러 ID
     * @param payload JSON 페이로드
     * @param status 상태
     * @param retryCount 재시도 횟수
     * @param maxRetry 최대 재시도 횟수
     * @param createdAt 생성일시
     * @param updatedAt 갱신일시
     * @param processedAt 처리일시
     * @param errorMessage 에러 메시지
     * @param version 낙관적 락 버전
     * @param idempotencyKey 멱등키 (String)
     * @return 재구성된 SellerAuthOutbox 인스턴스
     */
    public static SellerAuthOutbox reconstitute(
            SellerAuthOutboxId id,
            SellerId sellerId,
            String payload,
            SellerAuthOutboxStatus status,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new SellerAuthOutbox(
                id,
                sellerId,
                payload,
                status,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version,
                SellerAuthOutboxIdempotencyKey.of(idempotencyKey));
    }

    public boolean isNew() {
        return id.isNew();
    }

    /**
     * 처리 시작.
     *
     * <p>PENDING 또는 PROCESSING 상태에서만 호출 가능합니다.
     *
     * @param now 현재 시각 (updatedAt 갱신용)
     */
    public void startProcessing(Instant now) {
        if (!status.canProcess()) {
            throw new IllegalStateException("처리할 수 없는 상태입니다. 현재 상태: " + status);
        }
        this.status = SellerAuthOutboxStatus.PROCESSING;
        this.updatedAt = now;
    }

    /**
     * 처리 완료.
     *
     * @param now 처리 완료 시각
     */
    public void complete(Instant now) {
        this.status = SellerAuthOutboxStatus.COMPLETED;
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
            this.status = SellerAuthOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = SellerAuthOutboxStatus.PENDING;
        }
    }

    /**
     * 즉시 실패 처리 (재시도 없이).
     *
     * @param errorMessage 에러 메시지
     * @param now 현재 시각
     */
    public void fail(String errorMessage, Instant now) {
        this.status = SellerAuthOutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = now;
        this.updatedAt = now;
    }

    /**
     * 외부 API 실패 결과를 반영합니다.
     *
     * <p>canRetry=true이면 failAndRetry()를 호출하여 재시도 처리하고 (maxRetry 초과 시 자동 FAILED), canRetry=false이면
     * 즉시 FAILED 상태로 변경합니다.
     *
     * @param canRetry 재시도 가능 여부 (외부 API가 결정)
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
        if (this.status != SellerAuthOutboxStatus.PROCESSING) {
            throw new IllegalStateException("타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = SellerAuthOutboxStatus.PENDING;
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
        if (this.status != SellerAuthOutboxStatus.PROCESSING) {
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

    /** SellerId 설정. */
    public void assignSellerId(SellerId sellerId) {
        this.sellerId = sellerId;
    }

    // Getters
    public SellerAuthOutboxId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public Long sellerIdValue() {
        return sellerId.value();
    }

    public String payload() {
        return payload;
    }

    public SellerAuthOutboxStatus status() {
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

    public SellerAuthOutboxIdempotencyKey idempotencyKey() {
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
