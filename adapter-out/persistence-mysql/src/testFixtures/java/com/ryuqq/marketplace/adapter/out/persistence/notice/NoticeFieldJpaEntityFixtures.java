package com.ryuqq.marketplace.adapter.out.persistence.notice;

import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeFieldJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * NoticeFieldJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 NoticeFieldJpaEntity 관련 객체들을 생성합니다.
 */
public final class NoticeFieldJpaEntityFixtures {

    private NoticeFieldJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_NOTICE_CATEGORY_ID = 1L;
    public static final String DEFAULT_FIELD_CODE = "MATERIAL";
    public static final String DEFAULT_FIELD_NAME = "소재";
    public static final boolean DEFAULT_REQUIRED = true;
    public static final int DEFAULT_SORT_ORDER = 1;

    // ===== Entity Fixtures =====

    /** 필수 필드 Entity 생성. */
    public static NoticeFieldJpaEntity requiredFieldEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeFieldJpaEntity.create(
                seq,
                DEFAULT_NOTICE_CATEGORY_ID,
                DEFAULT_FIELD_CODE + "_" + seq,
                DEFAULT_FIELD_NAME,
                true,
                (int) seq,
                now,
                now);
    }

    /** ID를 지정한 필수 필드 Entity 생성. */
    public static NoticeFieldJpaEntity requiredFieldEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeFieldJpaEntity.create(
                id,
                DEFAULT_NOTICE_CATEGORY_ID,
                DEFAULT_FIELD_CODE + "_" + seq,
                DEFAULT_FIELD_NAME,
                true,
                (int) seq,
                now,
                now);
    }

    /** 선택 필드 Entity 생성. */
    public static NoticeFieldJpaEntity optionalFieldEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeFieldJpaEntity.create(
                seq,
                DEFAULT_NOTICE_CATEGORY_ID,
                "OPTIONAL_" + seq,
                "선택 필드",
                false,
                (int) seq,
                now,
                now);
    }

    /** 특정 카테고리 ID를 가진 필드 Entity 생성. */
    public static NoticeFieldJpaEntity fieldEntityWithCategoryId(Long noticeCategoryId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeFieldJpaEntity.create(
                seq,
                noticeCategoryId,
                DEFAULT_FIELD_CODE + "_" + seq,
                DEFAULT_FIELD_NAME,
                true,
                (int) seq,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static NoticeFieldJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeFieldJpaEntity.create(
                null,
                DEFAULT_NOTICE_CATEGORY_ID,
                DEFAULT_FIELD_CODE + "_" + seq,
                DEFAULT_FIELD_NAME,
                true,
                (int) seq,
                now,
                now);
    }

    /** 커스텀 정렬 순서를 가진 Entity 생성. */
    public static NoticeFieldJpaEntity entityWithSortOrder(int sortOrder) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeFieldJpaEntity.create(
                seq,
                DEFAULT_NOTICE_CATEGORY_ID,
                DEFAULT_FIELD_CODE + "_" + seq,
                DEFAULT_FIELD_NAME,
                true,
                sortOrder,
                now,
                now);
    }

    /** 커스텀 필드 코드와 이름을 가진 Entity 생성. */
    public static NoticeFieldJpaEntity entityWithCodeAndName(
            String fieldCode, String fieldName) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeFieldJpaEntity.create(
                seq,
                DEFAULT_NOTICE_CATEGORY_ID,
                fieldCode,
                fieldName,
                true,
                (int) seq,
                now,
                now);
    }

    /** 제조사 필드 Entity 생성. */
    public static NoticeFieldJpaEntity manufacturerFieldEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeFieldJpaEntity.create(
                seq,
                DEFAULT_NOTICE_CATEGORY_ID,
                "MANUFACTURER",
                "제조사",
                true,
                1,
                now,
                now);
    }

    /** 원산지 필드 Entity 생성. */
    public static NoticeFieldJpaEntity originFieldEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeFieldJpaEntity.create(
                seq,
                DEFAULT_NOTICE_CATEGORY_ID,
                "ORIGIN",
                "원산지",
                true,
                2,
                now,
                now);
    }

    /** 세탁방법 필드 Entity 생성. */
    public static NoticeFieldJpaEntity washingMethodFieldEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeFieldJpaEntity.create(
                seq,
                DEFAULT_NOTICE_CATEGORY_ID,
                "WASHING_METHOD",
                "세탁방법",
                false,
                3,
                now,
                now);
    }
}
