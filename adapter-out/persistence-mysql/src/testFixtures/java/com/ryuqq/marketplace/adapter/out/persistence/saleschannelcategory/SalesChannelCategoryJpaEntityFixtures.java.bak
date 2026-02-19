package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.SalesChannelCategoryJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SalesChannelCategoryJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SalesChannelCategoryJpaEntity 관련 객체들을 생성합니다.
 */
public final class SalesChannelCategoryJpaEntityFixtures {

    private SalesChannelCategoryJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "CAT001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_NAME = "테스트 카테고리";
    public static final int DEFAULT_DEPTH = 1;
    public static final String DEFAULT_PATH = "/CAT001";
    public static final int DEFAULT_SORT_ORDER = 1;
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_DISPLAY_PATH = "테스트 카테고리";

    // ===== Entity Fixtures =====

    /** 활성 상태의 SalesChannelCategory Entity 생성. */
    public static SalesChannelCategoryJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelCategoryJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                "CAT" + seq,
                "테스트 카테고리 " + seq,
                null,
                DEFAULT_DEPTH,
                "/CAT" + seq,
                DEFAULT_SORT_ORDER,
                false,
                "ACTIVE",
                "테스트 카테고리 " + seq,
                now,
                now);
    }

    /** ID를 지정한 활성 상태 Entity 생성. */
    public static SalesChannelCategoryJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelCategoryJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                "CAT" + seq,
                "테스트 카테고리 " + seq,
                null,
                DEFAULT_DEPTH,
                "/CAT" + seq,
                DEFAULT_SORT_ORDER,
                false,
                "ACTIVE",
                "테스트 카테고리 " + seq,
                now,
                now);
    }

    /** 비활성 상태 Entity 생성. */
    public static SalesChannelCategoryJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelCategoryJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                "CAT" + seq,
                "테스트 카테고리 " + seq,
                null,
                DEFAULT_DEPTH,
                "/CAT" + seq,
                DEFAULT_SORT_ORDER,
                false,
                "INACTIVE",
                "테스트 카테고리 " + seq,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static SalesChannelCategoryJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelCategoryJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                "CAT" + seq,
                "테스트 카테고리 " + seq,
                null,
                DEFAULT_DEPTH,
                "/CAT" + seq,
                DEFAULT_SORT_ORDER,
                false,
                "ACTIVE",
                "테스트 카테고리 " + seq,
                now,
                now);
    }

    /** 말단 카테고리 Entity 생성. */
    public static SalesChannelCategoryJpaEntity leafEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelCategoryJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                "CAT" + seq,
                "말단 카테고리 " + seq,
                100L,
                3,
                "/CAT001/CAT002/CAT" + seq,
                DEFAULT_SORT_ORDER,
                true,
                "ACTIVE",
                "상위 > 중간 > 말단 카테고리 " + seq,
                now,
                now);
    }

    /** 하위 카테고리 Entity 생성. */
    public static SalesChannelCategoryJpaEntity childEntity(Long parentId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelCategoryJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                "CAT" + seq,
                "하위 카테고리 " + seq,
                parentId,
                2,
                "/CAT001/CAT" + seq,
                DEFAULT_SORT_ORDER,
                false,
                "ACTIVE",
                "상위 > 하위 카테고리 " + seq,
                now,
                now);
    }

    /** 특정 SalesChannel의 Entity 생성. */
    public static SalesChannelCategoryJpaEntity entityWithSalesChannel(Long salesChannelId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelCategoryJpaEntity.create(
                null,
                salesChannelId,
                "CAT" + seq,
                "테스트 카테고리 " + seq,
                null,
                DEFAULT_DEPTH,
                "/CAT" + seq,
                DEFAULT_SORT_ORDER,
                false,
                "ACTIVE",
                "테스트 카테고리 " + seq,
                now,
                now);
    }

    /** 특정 ExternalCategoryCode를 가진 Entity 생성. */
    public static SalesChannelCategoryJpaEntity entityWithExternalCode(
            Long salesChannelId, String externalCategoryCode) {
        Instant now = Instant.now();
        return SalesChannelCategoryJpaEntity.create(
                null,
                salesChannelId,
                externalCategoryCode,
                "테스트 카테고리",
                null,
                DEFAULT_DEPTH,
                "/" + externalCategoryCode,
                DEFAULT_SORT_ORDER,
                false,
                "ACTIVE",
                "테스트 카테고리",
                now,
                now);
    }

    /** 특정 Depth를 가진 Entity 생성. */
    public static SalesChannelCategoryJpaEntity entityWithDepth(int depth) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        String path = depth > 1 ? "/CAT001/CAT" + depth : "/CAT" + depth;
        return SalesChannelCategoryJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                "CAT" + seq,
                "Depth " + depth + " 카테고리",
                depth > 1 ? 100L : null,
                depth,
                path,
                depth,
                depth >= 3,
                "ACTIVE",
                "카테고리 경로",
                now,
                now);
    }

    /** DisplayPath가 없는 Entity 생성. */
    public static SalesChannelCategoryJpaEntity entityWithoutDisplayPath() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return SalesChannelCategoryJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                "CAT" + seq,
                "테스트 카테고리 " + seq,
                null,
                DEFAULT_DEPTH,
                "/CAT" + seq,
                DEFAULT_SORT_ORDER,
                false,
                "ACTIVE",
                null,
                now,
                now);
    }

    /** 모든 필드를 커스터마이징한 Entity 생성. */
    public static SalesChannelCategoryJpaEntity customEntity(
            Long id,
            Long salesChannelId,
            String externalCategoryCode,
            String externalCategoryName,
            Long parentId,
            int depth,
            String path,
            int sortOrder,
            boolean leaf,
            String status,
            String displayPath) {
        Instant now = Instant.now();
        return SalesChannelCategoryJpaEntity.create(
                id,
                salesChannelId,
                externalCategoryCode,
                externalCategoryName,
                parentId,
                depth,
                path,
                sortOrder,
                leaf,
                status,
                displayPath,
                now,
                now);
    }
}
