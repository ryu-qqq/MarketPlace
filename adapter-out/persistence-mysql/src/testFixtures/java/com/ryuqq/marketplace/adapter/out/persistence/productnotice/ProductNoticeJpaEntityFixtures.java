package com.ryuqq.marketplace.adapter.out.persistence.productnotice;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ProductNoticeJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ProductNoticeJpaEntity 관련 객체들을 생성합니다.
 */
public final class ProductNoticeJpaEntityFixtures {

    private ProductNoticeJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final Long DEFAULT_NOTICE_CATEGORY_ID = 10L;
    public static final Long DEFAULT_NOTICE_FIELD_ID = 100L;
    public static final String DEFAULT_FIELD_VALUE = "기본 고시정보 값";

    // ===== ProductNoticeJpaEntity Fixtures =====

    /** 기본 ProductNotice Entity 생성 (ID null). */
    public static ProductNoticeJpaEntity newEntity() {
        Instant now = Instant.now();
        return ProductNoticeJpaEntity.create(
                null, DEFAULT_PRODUCT_GROUP_ID, DEFAULT_NOTICE_CATEGORY_ID, now, now);
    }

    /** 특정 ProductGroupId를 가진 ProductNotice Entity 생성. */
    public static ProductNoticeJpaEntity newEntity(Long productGroupId) {
        Instant now = Instant.now();
        return ProductNoticeJpaEntity.create(
                null, productGroupId, DEFAULT_NOTICE_CATEGORY_ID, now, now);
    }

    /** ID를 지정한 ProductNotice Entity 생성. */
    public static ProductNoticeJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return ProductNoticeJpaEntity.create(
                id, DEFAULT_PRODUCT_GROUP_ID, DEFAULT_NOTICE_CATEGORY_ID, now, now);
    }

    /** ID와 ProductGroupId를 지정한 ProductNotice Entity 생성. */
    public static ProductNoticeJpaEntity activeEntity(Long id, Long productGroupId) {
        Instant now = Instant.now();
        return ProductNoticeJpaEntity.create(
                id, productGroupId, DEFAULT_NOTICE_CATEGORY_ID, now, now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static ProductNoticeJpaEntity entityForPersist() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductNoticeJpaEntity.create(
                null, DEFAULT_PRODUCT_GROUP_ID + seq, DEFAULT_NOTICE_CATEGORY_ID, now, now);
    }

    // ===== ProductNoticeEntryJpaEntity Fixtures =====

    /** 기본 ProductNoticeEntry Entity 생성. */
    public static ProductNoticeEntryJpaEntity defaultEntryEntity(Long productNoticeId) {
        return ProductNoticeEntryJpaEntity.create(
                null, productNoticeId, DEFAULT_NOTICE_FIELD_ID, DEFAULT_FIELD_VALUE);
    }

    /** 특정 필드를 가진 ProductNoticeEntry Entity 생성. */
    public static ProductNoticeEntryJpaEntity entryEntity(
            Long productNoticeId, Long noticeFieldId, String fieldValue) {
        return ProductNoticeEntryJpaEntity.create(null, productNoticeId, noticeFieldId, fieldValue);
    }

    /** ID를 지정한 ProductNoticeEntry Entity 생성. */
    public static ProductNoticeEntryJpaEntity savedEntryEntity(
            Long id, Long productNoticeId, Long noticeFieldId, String fieldValue) {
        return ProductNoticeEntryJpaEntity.create(id, productNoticeId, noticeFieldId, fieldValue);
    }

    /** 기본 항목들이 포함된 Entry 목록 생성. */
    public static List<ProductNoticeEntryJpaEntity> defaultEntryEntities(Long productNoticeId) {
        return List.of(
                entryEntity(productNoticeId, 100L, "제조국"),
                entryEntity(productNoticeId, 101L, "제조사"),
                entryEntity(productNoticeId, 102L, "품질보증기준"));
    }

    /** ID가 있는 항목들이 포함된 Entry 목록 생성 (toDomain 테스트용). */
    public static List<ProductNoticeEntryJpaEntity> savedEntryEntities(Long productNoticeId) {
        return List.of(
                savedEntryEntity(1001L, productNoticeId, 100L, "제조국"),
                savedEntryEntity(1002L, productNoticeId, 101L, "제조사"),
                savedEntryEntity(1003L, productNoticeId, 102L, "품질보증기준"));
    }

    /** 빈 Entry 목록. */
    public static List<ProductNoticeEntryJpaEntity> emptyEntries() {
        return List.of();
    }
}
