package com.ryuqq.marketplace.adapter.out.persistence.inboundorder;

import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderItemJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * InboundOrderItemJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 InboundOrderItemJpaEntity 관련 객체들을 생성합니다.
 */
public final class InboundOrderItemJpaEntityFixtures {

    private InboundOrderItemJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    public static final String DEFAULT_EXTERNAL_PRODUCT_ID = "EXT-PROD-001";
    public static final String DEFAULT_EXTERNAL_OPTION_ID = "EXT-OPT-001";
    public static final String DEFAULT_EXTERNAL_PRODUCT_NAME = "테스트 상품";
    public static final String DEFAULT_EXTERNAL_OPTION_NAME = "블랙/M";
    public static final String DEFAULT_EXTERNAL_IMAGE_URL = "https://example.com/image.jpg";
    public static final int DEFAULT_UNIT_PRICE = 25000;
    public static final int DEFAULT_QUANTITY = 2;
    public static final int DEFAULT_TOTAL_AMOUNT = 50000;
    public static final int DEFAULT_DISCOUNT_AMOUNT = 0;
    public static final int DEFAULT_PAYMENT_AMOUNT = 50000;
    public static final String DEFAULT_RECEIVER_NAME = "김수령";
    public static final String DEFAULT_RECEIVER_PHONE = "010-9876-5432";
    public static final String DEFAULT_RECEIVER_ZIPCODE = "06234";
    public static final String DEFAULT_RECEIVER_ADDRESS = "서울시 강남구 테헤란로 123";
    public static final String DEFAULT_RECEIVER_ADDRESS_DETAIL = "456호";
    public static final String DEFAULT_DELIVERY_REQUEST = "부재 시 문 앞에 놓아주세요";

    /** 매핑 전 아이템 Entity 생성 (ID null). */
    public static InboundOrderItemJpaEntity unmappedEntity(long inboundOrderId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundOrderItemJpaEntity.create(
                null,
                inboundOrderId,
                "EXT-PROD-" + seq,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                DEFAULT_UNIT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_TOTAL_AMOUNT,
                DEFAULT_DISCOUNT_AMOUNT,
                DEFAULT_PAYMENT_AMOUNT,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST,
                null,
                null,
                null,
                null,
                null,
                false,
                now,
                now);
    }

    /** 매핑 완료 아이템 Entity 생성 (ID null). */
    public static InboundOrderItemJpaEntity mappedEntity(long inboundOrderId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundOrderItemJpaEntity.create(
                null,
                inboundOrderId,
                "EXT-PROD-" + seq,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                DEFAULT_UNIT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_TOTAL_AMOUNT,
                DEFAULT_DISCOUNT_AMOUNT,
                DEFAULT_PAYMENT_AMOUNT,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST,
                300L,
                400L,
                1L,
                500L,
                "SKU-001",
                true,
                now,
                now);
    }

    /** 단위 테스트용 기본 Entity. */
    public static InboundOrderItemJpaEntity entity(long inboundOrderId) {
        Instant now = Instant.now();
        return InboundOrderItemJpaEntity.create(
                1L,
                inboundOrderId,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                DEFAULT_EXTERNAL_OPTION_ID,
                DEFAULT_EXTERNAL_PRODUCT_NAME,
                DEFAULT_EXTERNAL_OPTION_NAME,
                DEFAULT_EXTERNAL_IMAGE_URL,
                DEFAULT_UNIT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_TOTAL_AMOUNT,
                DEFAULT_DISCOUNT_AMOUNT,
                DEFAULT_PAYMENT_AMOUNT,
                DEFAULT_RECEIVER_NAME,
                DEFAULT_RECEIVER_PHONE,
                DEFAULT_RECEIVER_ZIPCODE,
                DEFAULT_RECEIVER_ADDRESS,
                DEFAULT_RECEIVER_ADDRESS_DETAIL,
                DEFAULT_DELIVERY_REQUEST,
                null,
                null,
                null,
                null,
                null,
                false,
                now,
                now);
    }
}
