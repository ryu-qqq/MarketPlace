package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct;

import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity.InboundProductJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * InboundProductJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 InboundProductJpaEntity 관련 객체들을 생성합니다.
 */
public final class InboundProductJpaEntityFixtures {

    private InboundProductJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_INBOUND_SOURCE_ID = 10L;
    public static final String DEFAULT_EXTERNAL_PRODUCT_CODE = "EXT-PROD-001";
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "EXT-BRAND-001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "EXT-CAT-001";
    public static final Long DEFAULT_INTERNAL_BRAND_ID = 100L;
    public static final Long DEFAULT_INTERNAL_CATEGORY_ID = 200L;
    public static final Long DEFAULT_INTERNAL_PRODUCT_GROUP_ID = 300L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_STATUS_RECEIVED = "RECEIVED";
    public static final String DEFAULT_STATUS_MAPPED = "MAPPED";
    public static final String DEFAULT_STATUS_CONVERTED = "CONVERTED";
    public static final Long DEFAULT_RESOLVED_SHIPPING_POLICY_ID = 50L;
    public static final Long DEFAULT_RESOLVED_REFUND_POLICY_ID = 60L;
    public static final Long DEFAULT_RESOLVED_NOTICE_CATEGORY_ID = 70L;

    // ===== Entity Fixtures =====

    /** RECEIVED 상태의 신규 수신 Entity 생성 (ID null, 매핑 정보 없음). */
    public static InboundProductJpaEntity receivedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundProductJpaEntity.create(
                null,
                DEFAULT_INBOUND_SOURCE_ID,
                "EXT-PROD-" + seq,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                null,
                null,
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_RECEIVED,
                null,
                null,
                null,
                now,
                now);
    }

    /** ID를 지정한 RECEIVED 상태 Entity 생성. */
    public static InboundProductJpaEntity receivedEntity(Long id) {
        Instant now = Instant.now();
        return InboundProductJpaEntity.create(
                id,
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_PRODUCT_CODE,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                null,
                null,
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_RECEIVED,
                null,
                null,
                null,
                now,
                now);
    }

    /** inboundSourceId와 externalProductCode를 지정한 RECEIVED 상태 Entity 생성. */
    public static InboundProductJpaEntity receivedEntity(
            Long inboundSourceId, String externalProductCode) {
        Instant now = Instant.now();
        return InboundProductJpaEntity.create(
                null,
                inboundSourceId,
                externalProductCode,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                null,
                null,
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_RECEIVED,
                null,
                null,
                null,
                now,
                now);
    }

    /** MAPPED 상태 Entity 생성 (내부 브랜드/카테고리 매핑 완료, 정책은 미해석). */
    public static InboundProductJpaEntity mappedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundProductJpaEntity.create(
                null,
                DEFAULT_INBOUND_SOURCE_ID,
                "EXT-PROD-" + seq,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_MAPPED,
                null,
                null,
                null,
                now,
                now);
    }

    /** ID를 지정한 MAPPED 상태 Entity 생성. */
    public static InboundProductJpaEntity mappedEntity(Long id) {
        Instant now = Instant.now();
        return InboundProductJpaEntity.create(
                id,
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_PRODUCT_CODE,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_MAPPED,
                DEFAULT_RESOLVED_SHIPPING_POLICY_ID,
                DEFAULT_RESOLVED_REFUND_POLICY_ID,
                DEFAULT_RESOLVED_NOTICE_CATEGORY_ID,
                now,
                now);
    }

    /** CONVERTED 상태 Entity 생성 (내부 ProductGroup 변환 완료). */
    public static InboundProductJpaEntity convertedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundProductJpaEntity.create(
                null,
                DEFAULT_INBOUND_SOURCE_ID,
                "EXT-PROD-" + seq,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_CONVERTED,
                DEFAULT_RESOLVED_SHIPPING_POLICY_ID,
                DEFAULT_RESOLVED_REFUND_POLICY_ID,
                DEFAULT_RESOLVED_NOTICE_CATEGORY_ID,
                now,
                now);
    }

    /** ID를 지정한 CONVERTED 상태 Entity 생성. */
    public static InboundProductJpaEntity convertedEntity(Long id) {
        Instant now = Instant.now();
        return InboundProductJpaEntity.create(
                id,
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_PRODUCT_CODE,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_CONVERTED,
                DEFAULT_RESOLVED_SHIPPING_POLICY_ID,
                DEFAULT_RESOLVED_REFUND_POLICY_ID,
                DEFAULT_RESOLVED_NOTICE_CATEGORY_ID,
                now,
                now);
    }

    /** 외부 브랜드 코드가 없는 Entity 생성 (매핑 불가 케이스). */
    public static InboundProductJpaEntity entityWithoutBrandCode() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundProductJpaEntity.create(
                null,
                DEFAULT_INBOUND_SOURCE_ID,
                "EXT-PROD-" + seq,
                null,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                null,
                null,
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_RECEIVED,
                null,
                null,
                null,
                now,
                now);
    }

    /** 외부 카테고리 코드가 없는 Entity 생성. */
    public static InboundProductJpaEntity entityWithoutCategoryCode() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundProductJpaEntity.create(
                null,
                DEFAULT_INBOUND_SOURCE_ID,
                "EXT-PROD-" + seq,
                DEFAULT_EXTERNAL_BRAND_CODE,
                null,
                null,
                null,
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_RECEIVED,
                null,
                null,
                null,
                now,
                now);
    }

    /** 단위 테스트용 기본 Entity (DEFAULT_ID 사용). */
    public static InboundProductJpaEntity entity() {
        Instant now = Instant.now();
        return InboundProductJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_PRODUCT_CODE,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_RECEIVED,
                DEFAULT_RESOLVED_SHIPPING_POLICY_ID,
                DEFAULT_RESOLVED_REFUND_POLICY_ID,
                DEFAULT_RESOLVED_NOTICE_CATEGORY_ID,
                now,
                now);
    }

    /** ID를 지정한 단위 테스트용 Entity. */
    public static InboundProductJpaEntity entity(Long id) {
        Instant now = Instant.now();
        return InboundProductJpaEntity.create(
                id,
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_PRODUCT_CODE,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_INTERNAL_CATEGORY_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_STATUS_RECEIVED,
                DEFAULT_RESOLVED_SHIPPING_POLICY_ID,
                DEFAULT_RESOLVED_REFUND_POLICY_ID,
                DEFAULT_RESOLVED_NOTICE_CATEGORY_ID,
                now,
                now);
    }
}
