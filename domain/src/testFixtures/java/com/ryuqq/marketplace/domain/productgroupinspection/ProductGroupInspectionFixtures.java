package com.ryuqq.marketplace.domain.productgroupinspection;

import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import com.ryuqq.marketplace.domain.productgroupinspection.id.InspectionOutboxId;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionOutboxStatus;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ProductGroupInspection 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ProductGroupInspectionOutbox 관련 객체들을 생성합니다.
 */
public final class ProductGroupInspectionFixtures {

    private ProductGroupInspectionFixtures() {}

    private static final AtomicLong SEQ = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final String DEFAULT_INSPECTION_RESULT_JSON =
            "{\"totalScore\":85,\"passed\":true}";
    public static final int DEFAULT_TOTAL_SCORE = 85;
    public static final boolean DEFAULT_PASSED = true;
    public static final String DEFAULT_ERROR_MESSAGE = "최대 재시도 횟수 초과";

    // ===== 신규(미영속) Outbox Fixtures =====

    /** PENDING 상태의 신규 Outbox 생성 (ID null). */
    public static ProductGroupInspectionOutbox newPendingOutbox() {
        return ProductGroupInspectionOutbox.forNew(DEFAULT_PRODUCT_GROUP_ID, Instant.now());
    }

    /** productGroupId를 지정하여 신규 Outbox 생성 (ID null). */
    public static ProductGroupInspectionOutbox newPendingOutbox(Long productGroupId) {
        return ProductGroupInspectionOutbox.forNew(productGroupId, Instant.now());
    }

    // ===== 재구성(reconstitute) Outbox Fixtures =====

    /** PENDING 상태의 reconstituted Outbox (ID 1L). */
    public static ProductGroupInspectionOutbox pendingOutbox() {
        return pendingOutbox(1L);
    }

    /** ID 지정 PENDING 상태 Outbox. */
    public static ProductGroupInspectionOutbox pendingOutbox(Long id) {
        Instant now = Instant.now();
        long seq = SEQ.getAndIncrement();
        return ProductGroupInspectionOutbox.reconstitute(
                InspectionOutboxId.of(id),
                DEFAULT_PRODUCT_GROUP_ID,
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
                "PGI:" + DEFAULT_PRODUCT_GROUP_ID + ":" + now.toEpochMilli() + ":" + seq);
    }

    /** PROCESSING 상태 Outbox. */
    public static ProductGroupInspectionOutbox processingOutbox() {
        Instant now = Instant.now();
        long seq = SEQ.getAndIncrement();
        return ProductGroupInspectionOutbox.reconstitute(
                InspectionOutboxId.of(1L),
                DEFAULT_PRODUCT_GROUP_ID,
                InspectionOutboxStatus.SENT,
                null,
                null,
                null,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                1L,
                "PGI:" + DEFAULT_PRODUCT_GROUP_ID + ":" + now.toEpochMilli() + ":" + seq);
    }

    /** PROCESSING 상태 Outbox (ID 지정). */
    public static ProductGroupInspectionOutbox processingOutbox(Long id) {
        Instant now = Instant.now();
        long seq = SEQ.getAndIncrement();
        return ProductGroupInspectionOutbox.reconstitute(
                InspectionOutboxId.of(id),
                DEFAULT_PRODUCT_GROUP_ID,
                InspectionOutboxStatus.SENT,
                null,
                null,
                null,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                1L,
                "PGI:" + DEFAULT_PRODUCT_GROUP_ID + ":" + now.toEpochMilli() + ":" + seq);
    }

    /** COMPLETED 상태 Outbox. */
    public static ProductGroupInspectionOutbox completedOutbox() {
        Instant now = Instant.now();
        long seq = SEQ.getAndIncrement();
        return ProductGroupInspectionOutbox.reconstitute(
                InspectionOutboxId.of(1L),
                DEFAULT_PRODUCT_GROUP_ID,
                InspectionOutboxStatus.COMPLETED,
                DEFAULT_INSPECTION_RESULT_JSON,
                DEFAULT_TOTAL_SCORE,
                DEFAULT_PASSED,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                2L,
                "PGI:" + DEFAULT_PRODUCT_GROUP_ID + ":" + now.toEpochMilli() + ":" + seq);
    }

    /** FAILED 상태 Outbox. */
    public static ProductGroupInspectionOutbox failedOutbox() {
        Instant now = Instant.now();
        long seq = SEQ.getAndIncrement();
        return ProductGroupInspectionOutbox.reconstitute(
                InspectionOutboxId.of(1L),
                DEFAULT_PRODUCT_GROUP_ID,
                InspectionOutboxStatus.FAILED,
                null,
                null,
                null,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                DEFAULT_ERROR_MESSAGE,
                3L,
                "PGI:" + DEFAULT_PRODUCT_GROUP_ID + ":" + now.toEpochMilli() + ":" + seq);
    }

    // ===== VO Fixtures =====

    public static InspectionOutboxId outboxId(Long value) {
        return InspectionOutboxId.of(value);
    }

    public static InspectionOutboxId newOutboxId() {
        return InspectionOutboxId.forNew();
    }
}
