package com.ryuqq.marketplace.application.outboundproduct;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import java.time.Instant;

/**
 * OmsProduct Application Command 테스트 Fixtures.
 *
 * <p>outboundproduct 관련 Command 파라미터 및 컨텍스트 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class OmsProductCommandFixtures {

    private OmsProductCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_OUTBOX_ID = 1L;

    // ===== StatusChangeContext Fixtures =====

    /** 기본 outboxId(1L)로 생성된 RetryContext. */
    public static StatusChangeContext<Long> retryContext() {
        return new StatusChangeContext<>(DEFAULT_OUTBOX_ID, Instant.now());
    }

    /** 지정한 outboxId로 생성된 RetryContext. */
    public static StatusChangeContext<Long> retryContext(Long outboxId) {
        return new StatusChangeContext<>(outboxId, Instant.now());
    }

    /** 지정한 outboxId와 changedAt으로 생성된 RetryContext. */
    public static StatusChangeContext<Long> retryContext(Long outboxId, Instant changedAt) {
        return new StatusChangeContext<>(outboxId, changedAt);
    }
}
