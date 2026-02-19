package com.ryuqq.marketplace.domain.selleradmin;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminAuthOutboxId;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminEmailOutboxId;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminAuthOutboxStatus;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminEmailOutboxStatus;
import java.time.Instant;

/**
 * SellerAdmin 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 SellerAdmin 관련 객체들을 생성합니다.
 */
public final class SellerAdminFixtures {

    private SellerAdminFixtures() {}

    // ===== 공통 상수 =====
    public static final String DEFAULT_SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";

    // ===== SellerAdminAuthOutbox Fixtures =====
    private static final String AUTH_IDEMPOTENCY_KEY_PREFIX = "SAAO";

    public static String defaultAuthOutboxPayload() {
        return "{\"sellerAdminId\":\"01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60\",\"loginId\":\"admin@test.com\",\"name\":\"홍길동\"}";
    }

    private static String generateAuthIdempotencyKey(String sellerAdminId, Instant createdAt) {
        String idValue = sellerAdminId != null ? sellerAdminId : "unknown";
        return AUTH_IDEMPOTENCY_KEY_PREFIX + ":" + idValue + ":" + createdAt.toEpochMilli();
    }

    public static SellerAdminAuthOutbox newSellerAdminAuthOutbox() {
        return SellerAdminAuthOutbox.forNew(
                SellerAdminId.of(DEFAULT_SELLER_ADMIN_ID),
                defaultAuthOutboxPayload(),
                CommonVoFixtures.now());
    }

    public static SellerAdminAuthOutbox pendingSellerAdminAuthOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAdminAuthOutbox.reconstitute(
                SellerAdminAuthOutboxId.of(1L),
                SellerAdminId.of(DEFAULT_SELLER_ADMIN_ID),
                defaultAuthOutboxPayload(),
                SellerAdminAuthOutboxStatus.PENDING,
                0,
                3,
                yesterday,
                yesterday,
                null,
                null,
                0L,
                generateAuthIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, yesterday));
    }

    public static SellerAdminAuthOutbox pendingSellerAdminAuthOutboxWithId() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAdminAuthOutbox.reconstitute(
                SellerAdminAuthOutboxId.of(1L),
                SellerAdminId.of(DEFAULT_SELLER_ADMIN_ID),
                defaultAuthOutboxPayload(),
                SellerAdminAuthOutboxStatus.PENDING,
                0,
                3,
                yesterday,
                yesterday,
                null,
                null,
                0L,
                generateAuthIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, yesterday));
    }

    public static SellerAdminAuthOutbox processingSellerAdminAuthOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAdminAuthOutbox.reconstitute(
                SellerAdminAuthOutboxId.of(2L),
                SellerAdminId.of(DEFAULT_SELLER_ADMIN_ID),
                defaultAuthOutboxPayload(),
                SellerAdminAuthOutboxStatus.PROCESSING,
                0,
                3,
                yesterday,
                yesterday,
                null,
                null,
                0L,
                generateAuthIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, yesterday));
    }

    public static SellerAdminAuthOutbox completedSellerAdminAuthOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        Instant now = CommonVoFixtures.now();
        return SellerAdminAuthOutbox.reconstitute(
                SellerAdminAuthOutboxId.of(3L),
                SellerAdminId.of(DEFAULT_SELLER_ADMIN_ID),
                defaultAuthOutboxPayload(),
                SellerAdminAuthOutboxStatus.COMPLETED,
                0,
                3,
                yesterday,
                now,
                now,
                null,
                0L,
                generateAuthIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, yesterday));
    }

    public static SellerAdminAuthOutbox failedSellerAdminAuthOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        Instant now = CommonVoFixtures.now();
        return SellerAdminAuthOutbox.reconstitute(
                SellerAdminAuthOutboxId.of(4L),
                SellerAdminId.of(DEFAULT_SELLER_ADMIN_ID),
                defaultAuthOutboxPayload(),
                SellerAdminAuthOutboxStatus.FAILED,
                3,
                3,
                yesterday,
                now,
                now,
                "인증 서버 연동 실패로 최대 재시도 초과",
                0L,
                generateAuthIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, yesterday));
    }

    public static SellerAdminAuthOutbox retriableSellerAdminAuthOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAdminAuthOutbox.reconstitute(
                SellerAdminAuthOutboxId.of(5L),
                SellerAdminId.of(DEFAULT_SELLER_ADMIN_ID),
                defaultAuthOutboxPayload(),
                SellerAdminAuthOutboxStatus.PENDING,
                1,
                3,
                yesterday,
                yesterday,
                null,
                "첫 번째 시도 실패",
                0L,
                generateAuthIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, yesterday));
    }

    public static SellerAdminAuthOutbox processingTimeoutSellerAdminAuthOutbox(long secondsAgo) {
        Instant createdAt = CommonVoFixtures.now().minusSeconds(secondsAgo);
        return SellerAdminAuthOutbox.reconstitute(
                SellerAdminAuthOutboxId.of(6L),
                SellerAdminId.of(DEFAULT_SELLER_ADMIN_ID),
                defaultAuthOutboxPayload(),
                SellerAdminAuthOutboxStatus.PROCESSING,
                0,
                3,
                createdAt,
                createdAt,
                null,
                null,
                0L,
                generateAuthIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, createdAt));
    }

    // ===== SellerAdminEmailOutbox Fixtures =====
    private static final String IDEMPOTENCY_KEY_PREFIX = "SAEO";

    public static String defaultEmailOutboxPayload() {
        return "{\"sellerAdminId\":\"01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60\",\"loginId\":\"admin@test.com\",\"name\":\"홍길동\"}";
    }

    private static String generateIdempotencyKey(Long sellerId, Instant createdAt) {
        long sellerIdValue = sellerId != null ? sellerId : 0L;
        return IDEMPOTENCY_KEY_PREFIX + ":" + sellerIdValue + ":" + createdAt.toEpochMilli();
    }

    public static SellerAdminEmailOutbox newSellerAdminEmailOutbox() {
        return SellerAdminEmailOutbox.forNew(
                CommonVoFixtures.defaultSellerId(),
                defaultEmailOutboxPayload(),
                CommonVoFixtures.now());
    }

    public static SellerAdminEmailOutbox newSellerAdminEmailOutbox(SellerId sellerId) {
        return SellerAdminEmailOutbox.forNew(
                sellerId, defaultEmailOutboxPayload(), CommonVoFixtures.now());
    }

    public static SellerAdminEmailOutbox pendingSellerAdminEmailOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAdminEmailOutbox.reconstitute(
                SellerAdminEmailOutboxId.of(1L),
                CommonVoFixtures.defaultSellerId(),
                defaultEmailOutboxPayload(),
                SellerAdminEmailOutboxStatus.PENDING,
                0,
                3,
                yesterday,
                yesterday,
                null,
                null,
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAdminEmailOutbox pendingSellerAdminEmailOutboxWithId() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAdminEmailOutbox.reconstitute(
                SellerAdminEmailOutboxId.of(1L),
                CommonVoFixtures.defaultSellerId(),
                defaultEmailOutboxPayload(),
                SellerAdminEmailOutboxStatus.PENDING,
                0,
                3,
                yesterday,
                yesterday,
                null,
                null,
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAdminEmailOutbox processingSellerAdminEmailOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAdminEmailOutbox.reconstitute(
                SellerAdminEmailOutboxId.of(2L),
                CommonVoFixtures.defaultSellerId(),
                defaultEmailOutboxPayload(),
                SellerAdminEmailOutboxStatus.PROCESSING,
                0,
                3,
                yesterday,
                yesterday,
                null,
                null,
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAdminEmailOutbox completedSellerAdminEmailOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        Instant now = CommonVoFixtures.now();
        return SellerAdminEmailOutbox.reconstitute(
                SellerAdminEmailOutboxId.of(3L),
                CommonVoFixtures.defaultSellerId(),
                defaultEmailOutboxPayload(),
                SellerAdminEmailOutboxStatus.COMPLETED,
                0,
                3,
                yesterday,
                now,
                now,
                null,
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAdminEmailOutbox failedSellerAdminEmailOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        Instant now = CommonVoFixtures.now();
        return SellerAdminEmailOutbox.reconstitute(
                SellerAdminEmailOutboxId.of(4L),
                CommonVoFixtures.defaultSellerId(),
                defaultEmailOutboxPayload(),
                SellerAdminEmailOutboxStatus.FAILED,
                3,
                3,
                yesterday,
                now,
                now,
                "이메일 발송 실패로 인한 최대 재시도 초과",
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAdminEmailOutbox retriableSellerAdminEmailOutbox() {
        Instant yesterday = CommonVoFixtures.yesterday();
        return SellerAdminEmailOutbox.reconstitute(
                SellerAdminEmailOutboxId.of(5L),
                CommonVoFixtures.defaultSellerId(),
                defaultEmailOutboxPayload(),
                SellerAdminEmailOutboxStatus.PENDING,
                1,
                3,
                yesterday,
                yesterday,
                null,
                "첫 번째 시도 실패",
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), yesterday));
    }

    public static SellerAdminEmailOutbox processingTimeoutSellerAdminEmailOutbox(long secondsAgo) {
        Instant createdAt = CommonVoFixtures.now().minusSeconds(secondsAgo);
        return SellerAdminEmailOutbox.reconstitute(
                SellerAdminEmailOutboxId.of(6L),
                CommonVoFixtures.defaultSellerId(),
                defaultEmailOutboxPayload(),
                SellerAdminEmailOutboxStatus.PROCESSING,
                0,
                3,
                createdAt,
                createdAt,
                null,
                null,
                0L,
                generateIdempotencyKey(CommonVoFixtures.defaultSellerId().value(), createdAt));
    }
}
