package com.ryuqq.marketplace.adapter.out.persistence.exchange;

import com.ryuqq.marketplace.adapter.out.persistence.exchange.entity.ExchangeClaimJpaEntity;
import java.time.Instant;

/**
 * ExchangeClaimJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ExchangeClaimJpaEntity 관련 객체들을 생성합니다.
 */
public final class ExchangeClaimJpaEntityFixtures {

    private ExchangeClaimJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_ID = "01900000-0000-7000-0000-000000000001";
    public static final String DEFAULT_CLAIM_NUMBER = "EXC-20260218-0001";
    public static final String DEFAULT_ORDER_ITEM_ID = "01900000-0000-7000-0000-000000000010";
    public static final long DEFAULT_SELLER_ID = 100L;
    public static final int DEFAULT_EXCHANGE_QTY = 1;
    public static final String DEFAULT_STATUS_REQUESTED = "REQUESTED";
    public static final String DEFAULT_STATUS_COLLECTING = "COLLECTING";
    public static final String DEFAULT_STATUS_COMPLETED = "COMPLETED";
    public static final String DEFAULT_STATUS_REJECTED = "REJECTED";
    public static final String DEFAULT_REASON_TYPE = "SIZE_CHANGE";
    public static final String DEFAULT_REASON_DETAIL = "사이즈가 맞지 않아 교환 요청합니다";
    public static final String DEFAULT_REQUESTED_BY = "buyer@example.com";
    public static final String DEFAULT_PROCESSED_BY = "admin@marketplace.com";
    public static final Long DEFAULT_ORIGINAL_PRODUCT_ID = 1000L;
    public static final String DEFAULT_ORIGINAL_SKU_CODE = "SKU-RED-M";
    public static final Long DEFAULT_TARGET_PRODUCT_GROUP_ID = 1001L;
    public static final Long DEFAULT_TARGET_PRODUCT_ID = 2001L;
    public static final String DEFAULT_TARGET_SKU_CODE = "SKU-RED-XL";
    public static final int DEFAULT_TARGET_QUANTITY = 1;
    public static final int DEFAULT_ORIGINAL_PRICE = 30000;
    public static final int DEFAULT_TARGET_PRICE = 35000;
    public static final int DEFAULT_PRICE_DIFFERENCE = 5000;
    public static final int DEFAULT_COLLECT_SHIPPING_FEE = 3000;
    public static final int DEFAULT_RESHIP_SHIPPING_FEE = 3000;
    public static final int DEFAULT_TOTAL_SHIPPING_FEE = 6000;
    public static final String DEFAULT_SHIPPING_FEE_PAYER = "BUYER";
    public static final String DEFAULT_CLAIM_SHIPMENT_ID = "01900000-0000-7000-0000-000000000099";

    // ===== Entity Fixtures =====

    /** REQUESTED 상태의 신규 교환 클레임 Entity 생성. */
    public static ExchangeClaimJpaEntity requestedEntity() {
        Instant now = Instant.now();
        return ExchangeClaimJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                DEFAULT_STATUS_REQUESTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                DEFAULT_ORIGINAL_PRODUCT_ID,
                DEFAULT_ORIGINAL_SKU_CODE,
                DEFAULT_TARGET_PRODUCT_GROUP_ID,
                DEFAULT_TARGET_PRODUCT_ID,
                DEFAULT_TARGET_SKU_CODE,
                DEFAULT_TARGET_QUANTITY,
                DEFAULT_ORIGINAL_PRICE,
                DEFAULT_TARGET_PRICE,
                DEFAULT_PRICE_DIFFERENCE,
                true,
                false,
                DEFAULT_COLLECT_SHIPPING_FEE,
                DEFAULT_RESHIP_SHIPPING_FEE,
                DEFAULT_TOTAL_SHIPPING_FEE,
                DEFAULT_SHIPPING_FEE_PAYER,
                DEFAULT_CLAIM_SHIPMENT_ID,
                null,
                DEFAULT_REQUESTED_BY,
                null,
                now.minusSeconds(3600),
                null,
                null,
                null,
                null,
                now,
                now);
    }

    /** REQUESTED 상태 Entity 생성 (ID 지정). */
    public static ExchangeClaimJpaEntity requestedEntity(String id) {
        Instant now = Instant.now();
        return ExchangeClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                DEFAULT_STATUS_REQUESTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                DEFAULT_ORIGINAL_PRODUCT_ID,
                DEFAULT_ORIGINAL_SKU_CODE,
                DEFAULT_TARGET_PRODUCT_GROUP_ID,
                DEFAULT_TARGET_PRODUCT_ID,
                DEFAULT_TARGET_SKU_CODE,
                DEFAULT_TARGET_QUANTITY,
                DEFAULT_ORIGINAL_PRICE,
                DEFAULT_TARGET_PRICE,
                DEFAULT_PRICE_DIFFERENCE,
                true,
                false,
                DEFAULT_COLLECT_SHIPPING_FEE,
                DEFAULT_RESHIP_SHIPPING_FEE,
                DEFAULT_TOTAL_SHIPPING_FEE,
                DEFAULT_SHIPPING_FEE_PAYER,
                DEFAULT_CLAIM_SHIPMENT_ID,
                null,
                DEFAULT_REQUESTED_BY,
                null,
                now.minusSeconds(3600),
                null,
                null,
                null,
                null,
                now,
                now);
    }

    /** COLLECTING 상태 Entity 생성. */
    public static ExchangeClaimJpaEntity collectingEntity() {
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);
        return ExchangeClaimJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                DEFAULT_STATUS_COLLECTING,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                DEFAULT_ORIGINAL_PRODUCT_ID,
                DEFAULT_ORIGINAL_SKU_CODE,
                DEFAULT_TARGET_PRODUCT_GROUP_ID,
                DEFAULT_TARGET_PRODUCT_ID,
                DEFAULT_TARGET_SKU_CODE,
                DEFAULT_TARGET_QUANTITY,
                DEFAULT_ORIGINAL_PRICE,
                DEFAULT_TARGET_PRICE,
                DEFAULT_PRICE_DIFFERENCE,
                true,
                false,
                DEFAULT_COLLECT_SHIPPING_FEE,
                DEFAULT_RESHIP_SHIPPING_FEE,
                DEFAULT_TOTAL_SHIPPING_FEE,
                DEFAULT_SHIPPING_FEE_PAYER,
                DEFAULT_CLAIM_SHIPMENT_ID,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                yesterday,
                now,
                null,
                null,
                null,
                yesterday,
                now);
    }

    /** COMPLETED 상태 Entity 생성. */
    public static ExchangeClaimJpaEntity completedEntity() {
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);
        return ExchangeClaimJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                DEFAULT_STATUS_COMPLETED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                DEFAULT_ORIGINAL_PRODUCT_ID,
                DEFAULT_ORIGINAL_SKU_CODE,
                DEFAULT_TARGET_PRODUCT_GROUP_ID,
                DEFAULT_TARGET_PRODUCT_ID,
                DEFAULT_TARGET_SKU_CODE,
                DEFAULT_TARGET_QUANTITY,
                DEFAULT_ORIGINAL_PRICE,
                DEFAULT_TARGET_PRICE,
                DEFAULT_PRICE_DIFFERENCE,
                true,
                false,
                DEFAULT_COLLECT_SHIPPING_FEE,
                DEFAULT_RESHIP_SHIPPING_FEE,
                DEFAULT_TOTAL_SHIPPING_FEE,
                DEFAULT_SHIPPING_FEE_PAYER,
                DEFAULT_CLAIM_SHIPMENT_ID,
                "ORDER-20260101-9999",
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                yesterday,
                yesterday,
                now,
                null,
                null,
                yesterday,
                now);
    }

    /** REJECTED 상태 Entity 생성. */
    public static ExchangeClaimJpaEntity rejectedEntity() {
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);
        return ExchangeClaimJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                DEFAULT_STATUS_REJECTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                DEFAULT_ORIGINAL_PRODUCT_ID,
                DEFAULT_ORIGINAL_SKU_CODE,
                DEFAULT_TARGET_PRODUCT_GROUP_ID,
                DEFAULT_TARGET_PRODUCT_ID,
                DEFAULT_TARGET_SKU_CODE,
                DEFAULT_TARGET_QUANTITY,
                DEFAULT_ORIGINAL_PRICE,
                DEFAULT_TARGET_PRICE,
                DEFAULT_PRICE_DIFFERENCE,
                true,
                false,
                DEFAULT_COLLECT_SHIPPING_FEE,
                DEFAULT_RESHIP_SHIPPING_FEE,
                DEFAULT_TOTAL_SHIPPING_FEE,
                DEFAULT_SHIPPING_FEE_PAYER,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                yesterday,
                now,
                null,
                null,
                null,
                yesterday,
                now);
    }

    /** 특정 상태를 가진 Entity 생성. */
    public static ExchangeClaimJpaEntity entityWithStatus(String id, String status) {
        Instant now = Instant.now();
        return ExchangeClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                status,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                DEFAULT_ORIGINAL_PRODUCT_ID,
                DEFAULT_ORIGINAL_SKU_CODE,
                DEFAULT_TARGET_PRODUCT_GROUP_ID,
                DEFAULT_TARGET_PRODUCT_ID,
                DEFAULT_TARGET_SKU_CODE,
                DEFAULT_TARGET_QUANTITY,
                DEFAULT_ORIGINAL_PRICE,
                DEFAULT_TARGET_PRICE,
                DEFAULT_PRICE_DIFFERENCE,
                true,
                false,
                DEFAULT_COLLECT_SHIPPING_FEE,
                DEFAULT_RESHIP_SHIPPING_FEE,
                DEFAULT_TOTAL_SHIPPING_FEE,
                DEFAULT_SHIPPING_FEE_PAYER,
                DEFAULT_CLAIM_SHIPMENT_ID,
                null,
                DEFAULT_REQUESTED_BY,
                null,
                now.minusSeconds(3600),
                null,
                null,
                null,
                null,
                now,
                now);
    }

    /** 특정 orderItemId를 가진 REQUESTED Entity 생성. */
    public static ExchangeClaimJpaEntity requestedEntityWithOrderItemId(
            String id, String orderItemId) {
        Instant now = Instant.now();
        return ExchangeClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                orderItemId,
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                DEFAULT_STATUS_REQUESTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                DEFAULT_ORIGINAL_PRODUCT_ID,
                DEFAULT_ORIGINAL_SKU_CODE,
                DEFAULT_TARGET_PRODUCT_GROUP_ID,
                DEFAULT_TARGET_PRODUCT_ID,
                DEFAULT_TARGET_SKU_CODE,
                DEFAULT_TARGET_QUANTITY,
                DEFAULT_ORIGINAL_PRICE,
                DEFAULT_TARGET_PRICE,
                DEFAULT_PRICE_DIFFERENCE,
                true,
                false,
                DEFAULT_COLLECT_SHIPPING_FEE,
                DEFAULT_RESHIP_SHIPPING_FEE,
                DEFAULT_TOTAL_SHIPPING_FEE,
                DEFAULT_SHIPPING_FEE_PAYER,
                DEFAULT_CLAIM_SHIPMENT_ID,
                null,
                DEFAULT_REQUESTED_BY,
                null,
                now.minusSeconds(3600),
                null,
                null,
                null,
                null,
                now,
                now);
    }

    /** AmountAdjustment가 null인 간소화된 Entity 생성 (option/adjustment 없는 경우). */
    public static ExchangeClaimJpaEntity minimalEntity(String id) {
        Instant now = Instant.now();
        return ExchangeClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                DEFAULT_STATUS_REQUESTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                null,
                now.minusSeconds(3600),
                null,
                null,
                null,
                null,
                now,
                now);
    }
}
