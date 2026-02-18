package com.ryuqq.marketplace.adapter.out.persistence.product;

import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductOptionMappingJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ProductJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ProductJpaEntity 관련 객체들을 생성합니다.
 */
public final class ProductJpaEntityFixtures {

    private ProductJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final String DEFAULT_SKU_CODE = "SKU-TEST-001";
    public static final int DEFAULT_REGULAR_PRICE = 100000;
    public static final int DEFAULT_CURRENT_PRICE = 80000;
    public static final int DEFAULT_SALE_PRICE = 60000;
    public static final int DEFAULT_DISCOUNT_RATE = 25;
    public static final int DEFAULT_STOCK_QUANTITY = 100;
    public static final int DEFAULT_SORT_ORDER = 1;
    public static final String DEFAULT_STATUS = "ACTIVE";

    // ===== Entity Fixtures =====

    /** ACTIVE 상태의 Product Entity 생성. */
    public static ProductJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SKU_CODE + "-" + seq,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_SALE_PRICE,
                DEFAULT_DISCOUNT_RATE,
                DEFAULT_STOCK_QUANTITY,
                DEFAULT_STATUS,
                (int) seq,
                now,
                now);
    }

    /** ID를 지정한 ACTIVE 상태 Product Entity 생성. */
    public static ProductJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                id,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SKU_CODE + "-" + seq,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_SALE_PRICE,
                DEFAULT_DISCOUNT_RATE,
                DEFAULT_STOCK_QUANTITY,
                DEFAULT_STATUS,
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** 특정 ProductGroupId를 가진 ACTIVE 상태 Product Entity 생성. */
    public static ProductJpaEntity activeEntity(Long id, Long productGroupId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                id,
                productGroupId,
                DEFAULT_SKU_CODE + "-" + seq,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_SALE_PRICE,
                DEFAULT_DISCOUNT_RATE,
                DEFAULT_STOCK_QUANTITY,
                DEFAULT_STATUS,
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** INACTIVE 상태의 Product Entity 생성. */
    public static ProductJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SKU_CODE + "-" + seq,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_SALE_PRICE,
                DEFAULT_DISCOUNT_RATE,
                DEFAULT_STOCK_QUANTITY,
                "INACTIVE",
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** SOLDOUT 상태의 Product Entity 생성. */
    public static ProductJpaEntity soldOutEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SKU_CODE + "-" + seq,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_SALE_PRICE,
                DEFAULT_DISCOUNT_RATE,
                0,
                "SOLDOUT",
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** ID를 지정한 SOLDOUT 상태의 Product Entity 생성. */
    public static ProductJpaEntity soldOutEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                id,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SKU_CODE + "-" + seq,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_SALE_PRICE,
                DEFAULT_DISCOUNT_RATE,
                0,
                "SOLDOUT",
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** DELETED 상태의 Product Entity 생성. */
    public static ProductJpaEntity deletedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SKU_CODE + "-" + seq,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_SALE_PRICE,
                DEFAULT_DISCOUNT_RATE,
                DEFAULT_STOCK_QUANTITY,
                "DELETED",
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** 세일 가격 없는 Product Entity 생성. */
    public static ProductJpaEntity entityWithoutSalePrice() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SKU_CODE + "-" + seq,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                null,
                0,
                DEFAULT_STOCK_QUANTITY,
                DEFAULT_STATUS,
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** ID를 지정한 세일 가격 없는 Product Entity 생성. */
    public static ProductJpaEntity entityWithoutSalePrice(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                id,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SKU_CODE + "-" + seq,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                null,
                0,
                DEFAULT_STOCK_QUANTITY,
                DEFAULT_STATUS,
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static ProductJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SKU_CODE + "-" + seq,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_SALE_PRICE,
                DEFAULT_DISCOUNT_RATE,
                DEFAULT_STOCK_QUANTITY,
                DEFAULT_STATUS,
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** 저장된 Entity (ID 포함). */
    public static ProductJpaEntity savedEntity(Long id) {
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                id,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SKU_CODE,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_SALE_PRICE,
                DEFAULT_DISCOUNT_RATE,
                DEFAULT_STOCK_QUANTITY,
                DEFAULT_STATUS,
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    // ===== ProductOptionMapping Fixtures =====

    /** 기본 ProductOptionMappingJpaEntity 생성. */
    public static ProductOptionMappingJpaEntity defaultOptionMappingEntity() {
        return ProductOptionMappingJpaEntity.create(null, DEFAULT_ID, 100L);
    }

    /** 특정 productId를 가진 ProductOptionMappingJpaEntity 생성. */
    public static ProductOptionMappingJpaEntity optionMappingEntity(
            Long productId, Long sellerOptionValueId) {
        return ProductOptionMappingJpaEntity.create(null, productId, sellerOptionValueId);
    }

    /** ID가 있는 ProductOptionMappingJpaEntity 생성 (toDomain 테스트용). */
    public static ProductOptionMappingJpaEntity savedOptionMappingEntity(
            Long id, Long productId, Long sellerOptionValueId) {
        return ProductOptionMappingJpaEntity.create(id, productId, sellerOptionValueId);
    }

    /** 빈 옵션 매핑 목록. */
    public static List<ProductOptionMappingJpaEntity> emptyOptionMappings() {
        return List.of();
    }

    /** 단일 옵션 매핑 목록. */
    public static List<ProductOptionMappingJpaEntity> singleOptionMappings(Long productId) {
        return List.of(optionMappingEntity(productId, 100L));
    }

    /** ID가 있는 단일 옵션 매핑 목록 (toDomain 테스트용). */
    public static List<ProductOptionMappingJpaEntity> savedSingleOptionMappings(
            Long mappingId, Long productId) {
        return List.of(savedOptionMappingEntity(mappingId, productId, 100L));
    }
}
