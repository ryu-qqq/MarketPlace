package com.ryuqq.marketplace.adapter.out.persistence.outboundsync;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.composite.SyncHistoryCompositeDto;
import java.time.Instant;
import java.util.List;

/**
 * SyncHistoryCompositeDto 테스트 Fixtures.
 *
 * <p>연동 이력 Composition 조회 테스트용 DTO 생성.
 */
public final class SyncHistoryCompositeDtoFixtures {

    private SyncHistoryCompositeDtoFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_OUTBOX_ID = 1L;
    public static final String DEFAULT_SHOP_NAME = "테스트 외부몰";
    public static final String DEFAULT_ACCOUNT_ID = "test-account-001";
    public static final String DEFAULT_EXTERNAL_PRODUCT_ID = "EXT-PROD-001";
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PROCESSING = "PROCESSING";

    // ========================================================================
    // SyncHistoryCompositeDto Fixtures
    // ========================================================================

    /** COMPLETED 상태의 기본 SyncHistoryCompositeDto. */
    public static SyncHistoryCompositeDto completedDto() {
        return completedDto(DEFAULT_OUTBOX_ID);
    }

    /** 지정 outboxId의 COMPLETED 상태 SyncHistoryCompositeDto. */
    public static SyncHistoryCompositeDto completedDto(Long outboxId) {
        Instant now = Instant.now();
        return new SyncHistoryCompositeDto(
                outboxId,
                DEFAULT_SHOP_NAME,
                DEFAULT_ACCOUNT_ID,
                STATUS_COMPLETED,
                DEFAULT_RETRY_COUNT,
                null,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                now.minusSeconds(7200),
                now.minusSeconds(3600));
    }

    /** FAILED 상태의 SyncHistoryCompositeDto. */
    public static SyncHistoryCompositeDto failedDto(Long outboxId) {
        Instant now = Instant.now();
        return new SyncHistoryCompositeDto(
                outboxId,
                DEFAULT_SHOP_NAME,
                DEFAULT_ACCOUNT_ID,
                STATUS_FAILED,
                3,
                "외부 채널 연동 최대 재시도 초과",
                null,
                now.minusSeconds(14400),
                now.minusSeconds(3600));
    }

    /** PENDING 상태의 SyncHistoryCompositeDto (처리 전). */
    public static SyncHistoryCompositeDto pendingDto(Long outboxId) {
        Instant now = Instant.now();
        return new SyncHistoryCompositeDto(
                outboxId,
                DEFAULT_SHOP_NAME,
                DEFAULT_ACCOUNT_ID,
                STATUS_PENDING,
                0,
                null,
                null,
                now.minusSeconds(1800),
                null);
    }

    /** shopName과 accountId가 null인 SyncHistoryCompositeDto (shop 미조인 케이스). */
    public static SyncHistoryCompositeDto dtoWithNullShopInfo(Long outboxId) {
        Instant now = Instant.now();
        return new SyncHistoryCompositeDto(
                outboxId,
                null,
                null,
                STATUS_COMPLETED,
                DEFAULT_RETRY_COUNT,
                null,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                now.minusSeconds(3600),
                now);
    }

    /** externalProductId가 null인 SyncHistoryCompositeDto (미등록 상품 케이스). */
    public static SyncHistoryCompositeDto dtoWithNullExternalProductId(Long outboxId) {
        Instant now = Instant.now();
        return new SyncHistoryCompositeDto(
                outboxId,
                DEFAULT_SHOP_NAME,
                DEFAULT_ACCOUNT_ID,
                STATUS_PENDING,
                0,
                null,
                null,
                now.minusSeconds(600),
                null);
    }

    /** 목록 형태의 SyncHistoryCompositeDto 생성 (COMPLETED 상태). */
    public static List<SyncHistoryCompositeDto> completedDtoList(int count) {
        return java.util.stream.LongStream.rangeClosed(1, count)
                .mapToObj(i -> completedDto(DEFAULT_OUTBOX_ID + i - 1))
                .toList();
    }
}
