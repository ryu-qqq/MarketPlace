package com.ryuqq.marketplace.domain.legacyconversion;

import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacySellerIdMapping;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyConversionOutboxId;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderConversionOutboxId;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderIdMappingId;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyProductIdMappingId;
import com.ryuqq.marketplace.domain.legacyconversion.vo.LegacyConversionOutboxStatus;
import java.time.Instant;

/**
 * LegacyConversion 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 LegacyConversionOutbox, LegacyProductIdMapping, LegacyOrderConversionOutbox,
 * LegacyOrderIdMapping, LegacySellerIdMapping 관련 객체들을 생성합니다.
 */
public final class LegacyConversionFixtures {

    private LegacyConversionFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_OUTBOX_ID = 1L;
    public static final Long DEFAULT_LEGACY_PRODUCT_GROUP_ID = 100L;
    public static final Long DEFAULT_LEGACY_PRODUCT_ID = 200L;
    public static final Long DEFAULT_INTERNAL_PRODUCT_ID = 300L;
    public static final Long DEFAULT_INTERNAL_PRODUCT_GROUP_ID = 400L;
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final long DEFAULT_VERSION = 0L;

    public static final Long DEFAULT_ORDER_OUTBOX_ID = 10L;
    public static final Long DEFAULT_LEGACY_ORDER_ID = 1000L;
    public static final Long DEFAULT_LEGACY_PAYMENT_ID = 2000L;
    public static final String DEFAULT_INTERNAL_ORDER_ID = "ORD-2024-00001";
    public static final Long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_CHANNEL_NAME = "NAVER";
    public static final Long DEFAULT_ORDER_MAPPING_ID = 20L;

    public static final Long DEFAULT_LEGACY_SELLER_ID = 50L;
    public static final Long DEFAULT_INTERNAL_SELLER_ID = 1L;
    public static final String DEFAULT_SELLER_NAME = "테스트 셀러";

    // ========================================================================
    // LegacyConversionOutbox Fixtures
    // ========================================================================

    /** 신규 PENDING 상태 Outbox 생성 (ID 없음). */
    public static LegacyConversionOutbox newPendingOutbox() {
        return LegacyConversionOutbox.forNew(DEFAULT_LEGACY_PRODUCT_GROUP_ID, Instant.now());
    }

    /** 특정 legacyProductGroupId로 신규 PENDING Outbox 생성. */
    public static LegacyConversionOutbox newPendingOutbox(long legacyProductGroupId) {
        return LegacyConversionOutbox.forNew(legacyProductGroupId, Instant.now());
    }

    /** ID가 있는 PENDING 상태 Outbox 재구성. */
    public static LegacyConversionOutbox pendingOutbox() {
        return pendingOutbox(DEFAULT_OUTBOX_ID);
    }

    /** ID를 지정한 PENDING 상태 Outbox 재구성. */
    public static LegacyConversionOutbox pendingOutbox(Long id) {
        Instant now = Instant.now();
        return LegacyConversionOutbox.reconstitute(
                LegacyConversionOutboxId.of(id),
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                LegacyConversionOutboxStatus.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** PROCESSING 상태 Outbox 재구성. */
    public static LegacyConversionOutbox processingOutbox() {
        Instant now = Instant.now();
        return LegacyConversionOutbox.reconstitute(
                LegacyConversionOutboxId.of(DEFAULT_OUTBOX_ID),
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                LegacyConversionOutboxStatus.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                1L);
    }

    /** COMPLETED 상태 Outbox 재구성. */
    public static LegacyConversionOutbox completedOutbox() {
        Instant now = Instant.now();
        return LegacyConversionOutbox.reconstitute(
                LegacyConversionOutboxId.of(DEFAULT_OUTBOX_ID),
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                LegacyConversionOutboxStatus.COMPLETED,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                2L);
    }

    /** FAILED 상태 Outbox 재구성. */
    public static LegacyConversionOutbox failedOutbox() {
        Instant now = Instant.now();
        return LegacyConversionOutbox.reconstitute(
                LegacyConversionOutboxId.of(DEFAULT_OUTBOX_ID),
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                LegacyConversionOutboxStatus.FAILED,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "최대 재시도 횟수 초과",
                3L);
    }

    /** 재시도 횟수가 있는 PENDING Outbox 재구성. */
    public static LegacyConversionOutbox retriedPendingOutbox(int retryCount) {
        Instant now = Instant.now();
        return LegacyConversionOutbox.reconstitute(
                LegacyConversionOutboxId.of(DEFAULT_OUTBOX_ID),
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                LegacyConversionOutboxStatus.PENDING,
                retryCount,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                "이전 시도 실패",
                (long) retryCount);
    }

    // ========================================================================
    // LegacyProductIdMapping Fixtures
    // ========================================================================

    /** 신규 LegacyProductIdMapping 생성 (ID 없음). */
    public static LegacyProductIdMapping newMapping() {
        return LegacyProductIdMapping.forNew(
                DEFAULT_LEGACY_PRODUCT_ID,
                DEFAULT_INTERNAL_PRODUCT_ID,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                Instant.now());
    }

    /** 특정 legacyProductId로 신규 매핑 생성. */
    public static LegacyProductIdMapping newMapping(long legacyProductId, long internalProductId) {
        return LegacyProductIdMapping.forNew(
                legacyProductId,
                internalProductId,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                Instant.now());
    }

    /** ID가 있는 LegacyProductIdMapping 재구성. */
    public static LegacyProductIdMapping mapping() {
        return mapping(1L);
    }

    /** ID를 지정한 LegacyProductIdMapping 재구성. */
    public static LegacyProductIdMapping mapping(Long id) {
        return LegacyProductIdMapping.reconstitute(
                LegacyProductIdMappingId.of(id),
                DEFAULT_LEGACY_PRODUCT_ID,
                DEFAULT_INTERNAL_PRODUCT_ID,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                Instant.now());
    }

    /** 특정 legacyProductGroupId에 속한 여러 매핑 생성. */
    public static LegacyProductIdMapping mappingWithGroup(
            Long id,
            long legacyProductId,
            long internalProductId,
            long legacyProductGroupId,
            long internalProductGroupId) {
        return LegacyProductIdMapping.reconstitute(
                LegacyProductIdMappingId.of(id),
                legacyProductId,
                internalProductId,
                legacyProductGroupId,
                internalProductGroupId,
                Instant.now());
    }

    // ========================================================================
    // LegacyOrderConversionOutbox Fixtures
    // ========================================================================

    /** 신규 PENDING 상태 주문 Outbox 생성 (ID 없음). */
    public static LegacyOrderConversionOutbox newPendingOrderOutbox() {
        return LegacyOrderConversionOutbox.forNew(
                DEFAULT_LEGACY_ORDER_ID, DEFAULT_LEGACY_PAYMENT_ID, Instant.now());
    }

    /** 특정 legacyOrderId, legacyPaymentId로 신규 PENDING 주문 Outbox 생성. */
    public static LegacyOrderConversionOutbox newPendingOrderOutbox(
            long legacyOrderId, long legacyPaymentId) {
        return LegacyOrderConversionOutbox.forNew(legacyOrderId, legacyPaymentId, Instant.now());
    }

    /** ID가 있는 PENDING 상태 주문 Outbox 재구성. */
    public static LegacyOrderConversionOutbox pendingOrderOutbox() {
        return pendingOrderOutbox(DEFAULT_ORDER_OUTBOX_ID);
    }

    /** ID를 지정한 PENDING 상태 주문 Outbox 재구성. */
    public static LegacyOrderConversionOutbox pendingOrderOutbox(Long id) {
        Instant now = Instant.now();
        return LegacyOrderConversionOutbox.reconstitute(
                LegacyOrderConversionOutboxId.of(id),
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                LegacyConversionOutboxStatus.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** PROCESSING 상태 주문 Outbox 재구성. */
    public static LegacyOrderConversionOutbox processingOrderOutbox() {
        Instant now = Instant.now();
        return LegacyOrderConversionOutbox.reconstitute(
                LegacyOrderConversionOutboxId.of(DEFAULT_ORDER_OUTBOX_ID),
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                LegacyConversionOutboxStatus.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                1L);
    }

    /** COMPLETED 상태 주문 Outbox 재구성. */
    public static LegacyOrderConversionOutbox completedOrderOutbox() {
        Instant now = Instant.now();
        return LegacyOrderConversionOutbox.reconstitute(
                LegacyOrderConversionOutboxId.of(DEFAULT_ORDER_OUTBOX_ID),
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                LegacyConversionOutboxStatus.COMPLETED,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                2L);
    }

    /** FAILED 상태 주문 Outbox 재구성. */
    public static LegacyOrderConversionOutbox failedOrderOutbox() {
        Instant now = Instant.now();
        return LegacyOrderConversionOutbox.reconstitute(
                LegacyOrderConversionOutboxId.of(DEFAULT_ORDER_OUTBOX_ID),
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                LegacyConversionOutboxStatus.FAILED,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "최대 재시도 횟수 초과",
                3L);
    }

    /** 재시도 횟수가 있는 PENDING 주문 Outbox 재구성. */
    public static LegacyOrderConversionOutbox retriedPendingOrderOutbox(int retryCount) {
        Instant now = Instant.now();
        return LegacyOrderConversionOutbox.reconstitute(
                LegacyOrderConversionOutboxId.of(DEFAULT_ORDER_OUTBOX_ID),
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                LegacyConversionOutboxStatus.PENDING,
                retryCount,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                "이전 시도 실패",
                (long) retryCount);
    }

    // ========================================================================
    // LegacyOrderIdMapping Fixtures
    // ========================================================================

    /** 신규 주문 ID 매핑 생성 (ID 없음). */
    public static LegacyOrderIdMapping newOrderMapping() {
        return LegacyOrderIdMapping.forNew(
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                DEFAULT_INTERNAL_ORDER_ID,
                1001L,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_CHANNEL_NAME,
                Instant.now());
    }

    /** ID가 있는 주문 ID 매핑 재구성. */
    public static LegacyOrderIdMapping orderMapping() {
        return orderMapping(DEFAULT_ORDER_MAPPING_ID);
    }

    /** ID를 지정한 주문 ID 매핑 재구성. */
    public static LegacyOrderIdMapping orderMapping(Long id) {
        return LegacyOrderIdMapping.reconstitute(
                LegacyOrderIdMappingId.of(id),
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                DEFAULT_INTERNAL_ORDER_ID,
                1001L,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_CHANNEL_NAME,
                Instant.now());
    }

    // ========================================================================
    // LegacySellerIdMapping Fixtures
    // ========================================================================

    /** 기본 셀러 ID 매핑 재구성. */
    public static LegacySellerIdMapping sellerMapping() {
        return LegacySellerIdMapping.reconstitute(
                1L, DEFAULT_LEGACY_SELLER_ID, DEFAULT_INTERNAL_SELLER_ID, DEFAULT_SELLER_NAME);
    }

    /** 특정 값으로 셀러 ID 매핑 재구성. */
    public static LegacySellerIdMapping sellerMapping(
            Long id, long legacySellerId, long internalSellerId, String sellerName) {
        return LegacySellerIdMapping.reconstitute(id, legacySellerId, internalSellerId, sellerName);
    }
}
