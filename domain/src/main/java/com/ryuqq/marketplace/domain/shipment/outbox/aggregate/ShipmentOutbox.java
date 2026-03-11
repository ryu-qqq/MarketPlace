package com.ryuqq.marketplace.domain.shipment.outbox.aggregate;

import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.outbox.id.ShipmentOutboxId;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxStatus;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import java.time.Instant;

/**
 * 배송 상태 변경 Outbox Aggregate.
 *
 * <p>배송 상태 변경 시 외부 판매채널(네이버커머스 등)에 동기화하기 위한 Outbox 패턴 구현체입니다.
 *
 * <p>Shipment 상태 변경과 같은 트랜잭션에서 Facade를 통해 생성되며, 스케줄러에 의해 비동기로 처리됩니다.
 *
 * <p><strong>동시성 제어</strong>:
 *
 * <ul>
 *   <li>version: 낙관적 락을 위한 버전 필드
 *   <li>updatedAt: PROCESSING 좀비 상태 감지를 위한 갱신 시각
 *   <li>idempotencyKey: 외부 API 호출 멱등성 보장
 * </ul>
 */
public class ShipmentOutbox {

    private static final int DEFAULT_MAX_RETRY = 3;

    private final ShipmentOutboxId id;
    private final OrderItemId orderItemId;
    private final ShipmentOutboxType outboxType;
    private ShipmentOutboxStatus status;
    private final String payload;
    private int retryCount;
    private final int maxRetry;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant processedAt;
    private String errorMessage;
    private long version;
    private final ShipmentOutboxIdempotencyKey idempotencyKey;

    private ShipmentOutbox(
            ShipmentOutboxId id,
            OrderItemId orderItemId,
            ShipmentOutboxType outboxType,
            ShipmentOutboxStatus status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            ShipmentOutboxIdempotencyKey idempotencyKey) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.outboxType = outboxType;
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
     * 새 배송 아웃박스 생성.
     *
     * @param orderItemId 주문상품 ID
     * @param outboxType 아웃박스 유형 (CONFIRM, SHIP, DELIVER, CANCEL)
     * @param payload JSON 페이로드 (송장번호, 택배사 등 추가 정보)
     * @param now 현재 시각
     * @return 새 ShipmentOutbox 인스턴스
     */
    public static ShipmentOutbox forNew(
            OrderItemId orderItemId, ShipmentOutboxType outboxType, String payload, Instant now) {
        ShipmentOutboxIdempotencyKey idempotencyKey =
                ShipmentOutboxIdempotencyKey.generate(orderItemId.value(), outboxType, now);
        return new ShipmentOutbox(
                ShipmentOutboxId.forNew(),
                orderItemId,
                outboxType,
                ShipmentOutboxStatus.PENDING,
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

    /** DB에서 재구성. */
    public static ShipmentOutbox reconstitute(
            ShipmentOutboxId id,
            OrderItemId orderItemId,
            ShipmentOutboxType outboxType,
            ShipmentOutboxStatus status,
            String payload,
            int retryCount,
            int maxRetry,
            Instant createdAt,
            Instant updatedAt,
            Instant processedAt,
            String errorMessage,
            long version,
            String idempotencyKey) {
        return new ShipmentOutbox(
                id,
                orderItemId,
                outboxType,
                status,
                payload,
                retryCount,
                maxRetry,
                createdAt,
                updatedAt,
                processedAt,
                errorMessage,
                version,
                ShipmentOutboxIdempotencyKey.of(idempotencyKey));
    }

    public boolean isNew() {
        return id.isNew();
    }

    /** 처리 시작. PENDING → PROCESSING. */
    public void startProcessing(Instant now) {
        if (!status.isPending()) {
            throw new IllegalStateException("PENDING 상태에서만 처리를 시작할 수 있습니다. 현재 상태: " + status);
        }
        this.status = ShipmentOutboxStatus.PROCESSING;
        this.updatedAt = now;
    }

    /** 처리 완료. PROCESSING → COMPLETED. */
    public void complete(Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 완료할 수 있습니다. 현재 상태: " + status);
        }
        this.status = ShipmentOutboxStatus.COMPLETED;
        this.processedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /** 처리 실패 및 재시도. 최대 재시도 초과 시 FAILED. */
    public void failAndRetry(String errorMessage, Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 실패 처리할 수 있습니다. 현재 상태: " + status);
        }
        this.retryCount++;
        this.errorMessage = errorMessage;
        this.updatedAt = now;

        if (this.retryCount >= this.maxRetry) {
            this.status = ShipmentOutboxStatus.FAILED;
            this.processedAt = now;
        } else {
            this.status = ShipmentOutboxStatus.PENDING;
        }
    }

    /** 즉시 실패 처리 (재시도 없이). */
    public void fail(String errorMessage, Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("PROCESSING 상태에서만 실패 처리할 수 있습니다. 현재 상태: " + status);
        }
        this.status = ShipmentOutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = now;
        this.updatedAt = now;
    }

    /** 외부 API 실패 결과를 반영합니다. */
    public void recordFailure(boolean canRetry, String errorMessage, Instant now) {
        if (canRetry) {
            failAndRetry(errorMessage, now);
        } else {
            fail(errorMessage, now);
        }
    }

    /** FAILED → PENDING 수동 재처리. */
    public void retry(Instant now) {
        if (!status.isFailed()) {
            throw new IllegalStateException("FAILED 상태에서만 재처리할 수 있습니다. 현재 상태: " + status);
        }
        this.status = ShipmentOutboxStatus.PENDING;
        this.retryCount = 0;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /** PROCESSING 타임아웃 복구. */
    public void recoverFromTimeout(Instant now) {
        if (!status.isProcessing()) {
            throw new IllegalStateException("타임아웃 복구는 PROCESSING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.status = ShipmentOutboxStatus.PENDING;
        this.updatedAt = now;
        this.errorMessage = "타임아웃으로 인한 복구";
    }

    public boolean canRetry() {
        return retryCount < maxRetry && status.canProcess();
    }

    public boolean shouldProcess() {
        return status.isPending();
    }

    // Getters
    public ShipmentOutboxId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public OrderItemId orderItemId() {
        return orderItemId;
    }

    public Long orderItemIdValue() {
        return orderItemId.value();
    }

    public ShipmentOutboxType outboxType() {
        return outboxType;
    }

    public ShipmentOutboxStatus status() {
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

    public void refreshVersion(long version) {
        this.version = version;
    }

    public ShipmentOutboxIdempotencyKey idempotencyKey() {
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
