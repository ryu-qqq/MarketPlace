package com.ryuqq.marketplace.domain.outboundsync.aggregate;

import com.ryuqq.marketplace.domain.outboundsync.id.OutboundSyncOutboxId;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;

/**
 * 외부 상품 연동 Outbox Aggregate.
 *
 * <p>외부 판매채널(네이버커머스, 세토프, 바이마, LF몰)로의 상품 연동을 위한 Outbox 패턴 구현체입니다.
 *
 * <p>검수 통과 시 CONNECTED 채널별로 생성되며, 비동기 이벤트 리스너 또는 스케줄러에 의해 처리됩니다.
 *
 * <p><strong>동시성 제어</strong>:
 *
 * <ul>
 *   <li>version: 낙관적 락을 위한 버전 필드
 *   <li>updatedAt: PROCESSING 좀비 상태 감지를 위한 갱신 시각
 *   <li>idempotencyKey: 외부 API 호출 멱등성 보장
 * </ul>
 */
public class OutboundSyncOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final OutboundSyncOutboxId id;
    private final ProductGroupId productGroupId;
    private final SalesChannelId salesChannelId;
    private final SellerId sellerId;
    private final SyncType syncType;
    private SyncStatus status;
    private final String payload;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final SyncOutboxIdempotencyKey idempotencyKey;

    private OutboundSyncOutbox(
            OutboundSyncOutboxId id,
            ProductGroupId productGroupId,
            SalesChannelId salesChannelId,
            SellerId sellerId,
            SyncType syncType,
            SyncStatus status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            SyncOutboxIdempotencyKey idempotencyKey) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.salesChannelId = salesChannelId;
        this.sellerId = sellerId;
        this.syncType = syncType;
        this.status = status;
        this.payload = payload;
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
     * @param productGroupId 상품그룹 ID
     * @param salesChannelId 판매채널 ID
     * @param sellerId 셀러 ID
     * @param syncType 연동 타입
     * @param payload JSON 페이로드
     * @param now 현재 시각
     * @return 새 OutboundSyncOutbox 인스턴스
     */
    public static OutboundSyncOutbox forNew(
            ProductGroupId productGroupId,
            SalesChannelId salesChannelId,
            SellerId sellerId,
            SyncType syncType,
            String payload,
            Instant now) {
        SyncOutboxIdempotencyKey idempotencyKey =
                SyncOutboxIdempotencyKey.generate(
                        productGroupId.value(), salesChannelId.value(), syncType, now);
        return new OutboundSyncOutbox(
                OutboundSyncOutboxId.forNew(),
                productGroupId,
                salesChannelId,
                sellerId,
                syncType,
                SyncStatus.PENDING,
                payload,
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
     * @param productGroupId 상품그룹 ID
     * @param salesChannelId 판매채널 ID
     * @param sellerId 셀러 ID
     * @param syncType 연동 타입
     * @param status 상태
     * @param payload JSON 페이로드
     * @param retryCount 재시도 횟수
     * @param maxRetry 최대 재시도 횟수
     * @param createdAt 생성일시
     * @param updatedAt 갱신일시
     * @param processedAt 처리일시
     * @param errorMessage 에러 메시지
     * @param version 낙관적 락 버전
     * @param idempotencyKey 멱등키 (String)
     * @return 재구성된 OutboundSyncOutbox 인스턴스
     */
    public static OutboundSyncOutbox reconstitute(
            OutboundSyncOutboxId id,
            ProductGroupId productGroupId,
            SalesChannelId salesChannelId,
            SellerId sellerId,
            SyncType syncType,
            SyncStatus status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new OutboundSyncOutbox(
                id,
                productGroupId,
                salesChannelId,
                sellerId,
                syncType,
                status,
                payload,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version,
                SyncOutboxIdempotencyKey.of(idempotencyKey));
    }

    public boolean isNew() {
        return id.isNew();
    }

    /**
     * 처리 시작.
     *
     * <p>PENDING 상태에서만 PROCESSING으로 전이할 수 있습니다.
     *
     * @param now 현재 시각 (updatedAt 갱신용)
     * @throws IllegalStateException PENDING 상태가 아닌 경우
     */
    public void startProcessing(Instant now) {
        if (!status.isPending()) {
            throw new IllegalStateException("PENDING 상태에서만 처리를 시작할 수 있습니다. 현재 상태: " + status);
        }
        this.status = SyncStatus.PROCESSING;
        this.updatedAt = now;
    }

    /**
     * 처리 완료.
     *
     * <p>PROCESSING 상태에서만 COMPLETED로 전이할 수 있습니다.
     *
     * @param now 처리 완료 시각
     * @throws IllegalStateException PROCESSING 상태가 아닌 경우
     */
    public void complete(Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 완료할 수 있습니다. 현재 상태: " + status);
        }
        this.status = SyncStatus.COMPLETED;
        this.processedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /**
     * 처리 실패 및 재시도.
     *
     * <p>PROCESSING 상태에서만 호출 가능합니다. 재시도 횟수를 증가시키고, 최대 재시도 횟수 초과 시 FAILED 상태로 변경합니다.
     *
     * @param errorMessage 에러 메시지
     * @param now 현재 시각
     * @throws IllegalStateException PROCESSING 상태가 아닌 경우
     */
    public void failAndRetry(String errorMessage, Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 실패 처리할 수 있습니다. 현재 상태: " + status);
        }
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;

        if (this.retryCount >= this.maxRetry) {
            this.status = SyncStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = SyncStatus.PENDING;
        }
    }

    /**
     * 즉시 실패 처리 (재시도 없이).
     *
     * <p>PROCESSING 상태에서만 호출 가능합니다.
     *
     * @param errorMessage 에러 메시지
     * @param now 현재 시각
     * @throws IllegalStateException PROCESSING 상태가 아닌 경우
     */
    public void fail(String errorMessage, Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 실패 처리할 수 있습니다. 현재 상태: " + status);
        }
        this.status = SyncStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = now;
        this.updatedAt = now;
    }

    /**
     * 외부 API 실패 결과를 반영합니다.
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
     * @param now 현재 시각
     */
    public void recoverFromTimeout(Instant now) {
        if (this.status != SyncStatus.PROCESSING) {
            throw new IllegalStateException("타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = SyncStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구";
    }

    /** 재시도 가능 여부. */
    public boolean canRetry() {
        return retryCount < maxRetry && status.canProcess();
    }

    /** 처리 대상 여부 (PENDING 상태). */
    public boolean shouldProcess() {
        return status.isPending();
    }

    // Getters
    public OutboundSyncOutboxId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public ProductGroupId productGroupId() {
        return productGroupId;
    }

    public Long productGroupIdValue() {
        return productGroupId.value();
    }

    public SalesChannelId salesChannelId() {
        return salesChannelId;
    }

    public Long salesChannelIdValue() {
        return salesChannelId.value();
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public Long sellerIdValue() {
        return sellerId.value();
    }

    public SyncType syncType() {
        return syncType;
    }

    public SyncStatus status() {
        return status;
    }

    public String payload() {
        return payload;
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

    public SyncOutboxIdempotencyKey idempotencyKey() {
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
